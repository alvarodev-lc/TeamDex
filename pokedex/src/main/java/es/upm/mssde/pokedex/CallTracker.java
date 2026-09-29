package es.upm.mssde.pokedex;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Enqueues Retrofit calls tied to an activity: results are dropped once it is finishing or destroyed. */
public final class CallTracker {

    private static final String LOG_TAG = "call_tracker";

    private final Activity activity;
    private final List<Call<?>> pendingCalls = new ArrayList<>();

    public CallTracker(Activity activity) {
        this.activity = activity;
    }

    public <T> void enqueue(Call<T> call, Consumer<T> onSuccess, @Nullable Runnable onFailure) {
        pendingCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> c, @NonNull Response<T> response) {
                pendingCalls.remove(c);
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
                T body = response.body();
                if (response.isSuccessful() && body != null) {
                    onSuccess.accept(body);
                } else {
                    Log.w(LOG_TAG, "Request failed with code " + response.code() + ": " + c.request().url());
                    if (onFailure != null) {
                        onFailure.run();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> c, @NonNull Throwable t) {
                pendingCalls.remove(c);
                if (c.isCanceled() || activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
                Log.w(LOG_TAG, "Request failed: " + c.request().url(), t);
                if (onFailure != null) {
                    onFailure.run();
                }
            }
        });
    }

    public void cancelAll() {
        for (Call<?> call : new ArrayList<>(pendingCalls)) {
            call.cancel();
        }
        pendingCalls.clear();
    }
}
