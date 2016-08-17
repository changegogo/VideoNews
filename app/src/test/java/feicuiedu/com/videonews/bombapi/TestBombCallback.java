package feicuiedu.com.videonews.bombapi;


import java.io.IOException;

import feicuiedu.com.videonews.bombapi.bombcall.BombCallback;
import feicuiedu.com.videonews.bombapi.bombcall.exception.BombHttpException;
import timber.log.Timber;

public abstract class TestBombCallback<T> implements BombCallback<T>{

    @Override public void businessError(BombHttpException e) {
        Timber.e("businessError %s", e.getErrorResult().getError());
    }

    @Override public void networkError(IOException e) {
        throw new RuntimeException(e);
    }

    @Override public void unexpectedError(Throwable t) {
        throw new RuntimeException(t);
    }
}
