package org.autojs.autojs.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stardust.app.FragmentPagerAdapterBuilder;
import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.core.permission.OnRequestPermissionsResultCallback;
import com.stardust.autojs.core.permission.PermissionRequestProxyActivity;
import com.stardust.autojs.core.permission.RequestPermissionCallbacks;
import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.pio.PFiles;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.BackPressedHandler;
import com.stardust.util.DeveloperUtils;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.button.RippleView;
import com.xuexiang.xui.widget.textview.marqueen.MarqueeFactory;
import com.xuexiang.xui.widget.textview.marqueen.MarqueeView;
import com.xuexiang.xui.widget.textview.marqueen.SimpleNoticeMF;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.external.foreground.ForegroundService;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.tool.AccessibilityServiceTool;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.floating.FloatyWindowManger;
import org.autojs.autojs.ui.log.LogActivity_;
import org.autojs.autojs.ui.main.community.CommunityFragment;
import org.autojs.autojs.ui.settings.SettingsActivity_;
import org.autojs.autojs.ui.settings.VideoSettingsActivity_;
import org.autojs.autojs.ui.update.VersionGuard;
import org.autojs.autojs.ui.widget.CommonMarkdownView;
import org.autojs.autojs.ui.widget.SearchViewItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements OnActivityResultDelegate.DelegateHost, BackPressedHandler.HostActivity, PermissionRequestProxyActivity {

    public static class DrawerOpenEvent {
        static DrawerOpenEvent SINGLETON = new DrawerOpenEvent();
    }
    private static final String  signal ="uyMt3t/FqNUjYvXE6KElfppO17L1Nzhm0mXlnsPBl1o=";
    private static final String LOG_TAG = "MainActivity";

    @ViewById(R.id.drawer_layout)
    LinearLayout mDrawerLayout;
    @ViewById(R.id.fab)
    FloatingActionButton mFab;
    @ViewById(R.id.sib)
    SimpleImageBanner sib;
    @ViewById(R.id.marquee)
    MarqueeView marquee;
    @ViewById(R.id.rvVideo)
    RelativeLayout rvVideo;

    private FragmentPagerAdapterBuilder.StoredFragmentPagerAdapter mPagerAdapter;
    private OnActivityResultDelegate.Mediator mActivityResultMediator = new OnActivityResultDelegate.Mediator();
    private RequestPermissionCallbacks mRequestPermissionCallbacks = new RequestPermissionCallbacks();
    private VersionGuard mVersionGuard;
    private BackPressedHandler.Observer mBackPressObserver = new BackPressedHandler.Observer();
    private SearchViewItem mSearchViewItem;
    private MenuItem mLogMenuItem;
    private boolean mDocsSearchItemExpanded;

    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "http://photocdn.sohu.com/tvmobilemvms/20150907/144160323071011277.jpg",//伪装者:胡歌演绎"痞子特工"
            "http://photocdn.sohu.com/tvmobilemvms/20150907/144158380433341332.jpg",//无心法师:生死离别!月牙遭虐杀
            "http://photocdn.sohu.com/tvmobilemvms/20150907/144160286644953923.jpg",//花千骨:尊上沦为花千骨
            "http://photocdn.sohu.com/tvmobilemvms/20150902/144115156939164801.jpg",//综艺饭:胖轩偷看夏天洗澡掀波澜
            "http://photocdn.sohu.com/tvmobilemvms/20150907/144159406950245847.jpg",//碟中谍4:阿汤哥高塔命悬一线,超越不可能
    };

    final List<String> datas = Arrays.asList("《赋得古原草送别》", "离离原上草，一岁一枯荣。", "野火烧不尽，春风吹又生。", "远芳侵古道，晴翠接荒城。", "又送王孙去，萋萋满别情。");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        checkPermissions();
        showAccessibilitySettingPromptIfDisabled();
        mVersionGuard = new VersionGuard(this);
//        showAnnunciationIfNeeded();
        EventBus.getDefault().register(this);
        applyDayNightMode();
    }

    private void initBanner(){
        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = "";
            list.add(item);
        }
        sib.setSource(list).startScroll();
    }

    private void initMarquee(){
        MarqueeFactory<TextView, String> marqueeFactory2 = new SimpleNoticeMF(this);
        marqueeFactory2.setOnItemClickListener((view, holder) -> {});
        marqueeFactory2.setData(datas);
        marquee.setMarqueeFactory(marqueeFactory2);
        marquee.setAnimDuration(15000);
        marquee.setInterval(16000);
        marquee.startFlipping();
    }

    @AfterViews
    void setUpViews() {
        setUpToolbar();
//        setUpTabViewPager();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        registerBackPressHandlers();
        ThemeColorManager.addViewBackground(findViewById(R.id.app_bar));
//        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                EventBus.getDefault().post(DrawerOpenEvent.SINGLETON);
//            }
//        });
        initBanner();
        initMarquee();
    }

//    private void showAnnunciationIfNeeded() {
//        if (!Pref.shouldShowAnnunciation()) {
//            return;
//        }
//        new CommonMarkdownView.DialogBuilder(this)
//                .padding(36, 0, 36, 0)
//                .markdown(PFiles.read(getResources().openRawResource(R.raw.annunciation)))
//                .title(R.string.text_annunciation)
//                .positiveText(R.string.ok)
//                .canceledOnTouchOutside(false)
//                .show();
//    }


    private void registerBackPressHandlers() {
//        mBackPressObserver.registerHandler(new DrawerAutoClose(mDrawerLayout, Gravity.START));
        mBackPressObserver.registerHandler(new BackPressedHandler.DoublePressExit(this, R.string.text_press_again_to_exit));
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void showAccessibilitySettingPromptIfDisabled() {
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(this)) {
            return;
        }
//        new NotAskAgainDialog.Builder(this, "MainActivity.accessibility")
//                .title(R.string.text_need_to_enable_accessibility_service)
//                .content(R.string.explain_accessibility_permission)
//                .positiveText(R.string.text_go_to_setting)
//                .negativeText(R.string.text_cancel)
//                .onPositive((dialog, which) ->
//                        AccessibilityServiceTool.enableAccessibilityService()
//                ).show();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
//        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.text_drawer_open,
//                R.string.text_drawer_close);
//        drawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(drawerToggle);
    }

//    private void setUpTabViewPager() {
//        TabLayout tabLayout = $(R.id.tab);
//        mPagerAdapter = new FragmentPagerAdapterBuilder(this)
//                .add(new MyScriptListFragment_(), R.string.text_file)
//                .add(new DocsFragment_(), R.string.text_tutorial)
//                .add(new CommunityFragment_(), R.string.text_community)
//                .add(new MarketFragment(), R.string.text_market)
//                .add(new TaskManagerFragment_(), R.string.text_manage)
//                .build();
//        mViewPager.setAdapter(mPagerAdapter);
//        tabLayout.setupWithViewPager(mViewPager);
//        setUpViewPagerFragmentBehaviors();
//    }


    @Click(R.id.rvVideo)
    void startVideoActivity() {
        startActivity(new Intent(this, VideoSettingsActivity_.class));
    }

    @Click(R.id.exit)
    public void exitCompletely() {
        finish();
        FloatyWindowManger.hideCircularMenu();
        ForegroundService.stop(this);
        stopService(new Intent(this, FloatyService.class));
        AutoJs.getInstance().getScriptEngineService().stopAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mVersionGuard.checkForDeprecatesAndUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityResultMediator.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestPermissionCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        if (getGrantResult(Manifest.permission.READ_EXTERNAL_STORAGE, permissions, grantResults) == PackageManager.PERMISSION_GRANTED) {
            Explorers.workspace().refreshAll();
        }
    }

    private int getGrantResult(String permission, String[] permissions, int[] grantResults) {
        int i = Arrays.asList(permissions).indexOf(permission);
        if (i < 0) {
            return 2;
        }
        return grantResults[i];
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            DeveloperUtils.verifyApk(this, signal, R.string.dex_crcs);
        }
    }


    @NonNull
    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mActivityResultMediator;
    }

    @Override
    public void onBackPressed() {
        if (!mBackPressObserver.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void addRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        mRequestPermissionCallbacks.addCallback(callback);
    }

    @Override
    public boolean removeRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        return mRequestPermissionCallbacks.removeCallback(callback);
    }


    @Override
    public BackPressedHandler.Observer getBackPressedObserver() {
        return mBackPressObserver;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mLogMenuItem = menu.findItem(R.id.action_log);
//        setUpSearchMenuItem(searchMenuItem);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_log) {
            startActivity(new Intent(this, SettingsActivity_.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onLoadUrl(CommunityFragment.LoadUrl loadUrl) {
//        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    private void setUpSearchMenuItem(MenuItem searchMenuItem) {
        mSearchViewItem = new SearchViewItem(this, searchMenuItem) {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
//                if (mViewPager.getCurrentItem() == 1) {
//                    mDocsSearchItemExpanded = true;
//                    mLogMenuItem.setIcon(R.drawable.ic_ali_up);
//                }
                return super.onMenuItemActionExpand(item);
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mDocsSearchItemExpanded) {
                    mDocsSearchItemExpanded = false;
                    mLogMenuItem.setIcon(R.drawable.ic_ali_log);
                }
                return super.onMenuItemActionCollapse(item);
            }
        };
        mSearchViewItem.setQueryCallback(this::submitQuery);
    }

    private void submitQuery(String query) {
        if (query == null) {
            EventBus.getDefault().post(QueryEvent.CLEAR);
            return;
        }
        QueryEvent event = new QueryEvent(query);
        EventBus.getDefault().post(event);
        if (event.shouldCollapseSearchView()) {
            mSearchViewItem.collapse();
        }
    }

    private void submitForwardQuery() {
        QueryEvent event = QueryEvent.FIND_FORWARD;
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}