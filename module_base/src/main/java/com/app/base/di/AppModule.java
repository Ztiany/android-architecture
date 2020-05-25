package com.app.base.di;

import android.app.Application;
import android.content.Context;

import com.android.base.app.dagger.ContextType;
import com.android.base.concurrent.DispatcherProvider;
import com.android.base.concurrent.DispatcherProviders;
import com.android.base.concurrent.SchedulerProvider;
import com.android.base.concurrent.SchedulerProviders;
import com.android.base.imageloader.ImageLoader;
import com.android.base.imageloader.ImageLoaderFactory;
import com.app.base.router.AppRouter;
import com.app.base.router.AppRouterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2018-11-01 10:33
 */
@Module
public class AppModule {

    private Context mContext;

    public AppModule(Application application) {
        mContext = application;
    }

    @Provides
    @Singleton
    @ContextType
    Context getAppContext() {
        return mContext;
    }

    @Provides
    @Singleton
    ImageLoader provideImageLoader() {
        return ImageLoaderFactory.getImageLoader();
    }

    @Provides
    @Singleton
    AppRouter provideAppRouter() {
        return new AppRouterImpl();
    }

    @Provides
    @Singleton
    SchedulerProvider provideSchedulerProvider() {
        return SchedulerProviders.newDefaultSchedulerProvider();
    }

    @Provides
    @Singleton
    DispatcherProvider provideDispatcherProvider() {
        return DispatcherProviders.newRxDispatchProvider();
    }

}
