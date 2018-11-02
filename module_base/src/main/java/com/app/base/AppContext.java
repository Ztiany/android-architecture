package com.app.base;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;

import com.android.base.app.AndroidBase;
import com.android.base.app.BaseAppContext;
import com.android.base.app.fragment.tools.FragmentHelper;
import com.android.base.app.ui.PageNumber;
import com.android.base.rx.SchedulerProvider;
import com.app.base.config.AppSettings;
import com.app.base.data.DataContext;
import com.app.base.data.ServiceFactory;
import com.app.base.errorhandle.ErrorHandler;
import com.app.base.router.AppRouter;
import com.app.base.router.RouterManager;
import com.app.base.widget.dialog.AppLoadingView;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2018-11-01 10:20
 */
public abstract class AppContext extends BaseAppContext implements HasSupportFragmentInjector, HasActivityInjector {

    private static AppContext sAppContext;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Inject
    ErrorHandler mErrorHandler;
    @Inject
    ServiceFactory mServiceFactory;
    @Inject
    AppRouter mAppRouter;
    @Inject
    SchedulerProvider mSchedulerProvider;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        initApp();
        configSDK();
        openDebugIfNeed();
    }

    private void initApp() {
        AppSettings.init(this);
        RouterManager.init(this);
        DataContext.init(this);

        FragmentHelper.setDefaultContainerId(R.id.common_container_id);//默认的Fragment容器id
        AndroidBase.get().registerLoadingFactory(AppLoadingView::new);
        PageNumber.setDefaultPageStart(1);
        PageNumber.setDefaultPageSize(20);
        injectAppContext();
    }

    private void configSDK() {
        //todo initialize push/tinker/analytics etc...
    }

    private void openDebugIfNeed() {
        DebugTools.init(this);
    }

    public static AppContext getContext() {
        return sAppContext;
    }

    public ErrorHandler errorHandler() {
        return mErrorHandler;
    }

    public ServiceFactory serviceFactory() {
        return mServiceFactory;
    }

    @SuppressWarnings("unused")
    public AppRouter appRouter() {
        return mAppRouter;
    }

    public SchedulerProvider schedulerProvider() {
        return mSchedulerProvider;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }

    protected abstract void injectAppContext();

}