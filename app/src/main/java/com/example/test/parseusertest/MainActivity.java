package com.example.test.parseusertest;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity {
  private final static String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final View init = findViewById(R.id.init);
    final View save = findViewById(R.id.save);

    init.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        init();
      }
    });

    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        save();
      }
    });
  }

  private void save() {
    final ProgressDialog progress = new ProgressDialog(this);
    progress.setCancelable(false);
    progress.setMessage("Saving user...");
    progress.show();

    final ParseUser user = ParseUser.getCurrentUser();
    user.saveInBackground().continueWithTask(new Continuation<Void, Task<Void>>() {
      @Override
      public Task<Void> then(Task<Void> task) throws Exception {
        progress.dismiss();
        if (task.isFaulted()) {
          Log.e(TAG, "Failed to save", task.getError());
          new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Save user failed")
                  .setMessage(task.getError().getMessage())
                  .setPositiveButton(android.R.string.ok, null)
                  .show();
        } else {
          new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Save user succeeded")
                  .setMessage(user.getObjectId())
                  .setPositiveButton(android.R.string.ok, null)
                  .show();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void init() {
    final ProgressDialog progress = new ProgressDialog(this);
    progress.setCancelable(false);
    progress.setMessage("Initializing parse...");
    progress.show();
    Log.d(TAG, "init");
    Parse.initialize(new Parse.Configuration.Builder(this)
      .applicationId(BuildConfig.PARSE_APP_ID)
      .clientKey(BuildConfig.PARSE_CLIENT_KEY)
      .server(BuildConfig.PARSE_URL)
      .build()
    );
    Log.d(TAG, "Parse.initialize succeeded");

    progress.setMessage("Enabling automatic user...");
    ParseUser.enableAutomaticUser();
    Log.d(TAG, "Parse.enableAutomaticUser succeeded");
    progress.setMessage("Saving user...");
    final ParseUser user = ParseUser.getCurrentUser();
    user.saveInBackground().continueWithTask(new Continuation<Void, Task<Void>>() {
      @Override
      public Task<Void> then(Task<Void> task) throws Exception {
        progress.dismiss();
        final ParseUser user = ParseUser.getCurrentUser();
        if (task.isFaulted()) {
          Log.e(TAG, "Failed to save user", task.getError());
          new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Failed to save user")
                  .setMessage(task.getError().getMessage())
                  .setPositiveButton(android.R.string.ok, null)
                  .show();
        } else {
          new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Init succeeded")
                  .setMessage(user.getObjectId())
                  .setPositiveButton(android.R.string.ok, null)
                  .show();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }
}
