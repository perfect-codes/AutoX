//视频号
launchApp('微信')
sleep(1000)
//toastLog('视频号')
//申请截屏权限
while(!images.requestScreenCapture())
//通过点赞判断当前页面是否为可运行页面
id('com.tencent.mm:id/cne').findOne()
var runtime = 1
var collect = false;
var comment = false;
var praise = false;
var comments //评论列表
var messages //私聊列表

loadSettings()

var pid = setInterval(function(){
    work()
},1)
setTimeout(function(){
    clearInterval(pid);
    endWithToastTotal();
}, runtime * 60 * 1000);

var i = 0
function work(){
    log('---work---')
    //视频号视频列表页面
    if(collect && praise && comment){
        //收藏+点赞+评论
        do_collect(false)
        sleep(1000)
        do_praise(true)
        sleep(1000)
    }else if(collect && praise && !comment){
        //收藏+点赞
        do_collect(false)
        sleep(1000)
        do_praise(false)
        sleep(1000)
    }else if(!collect && praise && comment){
        //点赞+评论
        do_praise(true)
        sleep(1000)
    }else if(collect && !praise && comment){
        //收藏+评论
        do_collect(true)
        sleep(1000)
    }else if(collect && !praise && !comment){
        //只收藏
        do_collect(false)
        sleep(1000)
    }else if(!collect && praise && !comment){
        //只点赞
        do_praise(false)
        sleep(1000)
    }else if(!collect && !praise && comment){
        //只评论
        sleep(1000)
    }
    scrollToNext()
    sleep(1000)
    if (id('com.tencent.mm:id/hya').exists()) {
        endWithToastTotal()
        exit();
    }
    // isRun = false
}

function loadSettings(){
    var videoSettingPath = '/data/user/0/com.fj.aux/files/video_settings.json'
    var commentSettingPath = '/data/user/0/com.fj.aux/files/comment_settings.json'
    // toastLog(files.exists(videoSettingPath))
    if(files.exists(videoSettingPath)){
        var vsStr = files.read(videoSettingPath);
        log(vsStr)
        var vsObj = JSON.parse(vsStr);
        runtime = vsObj['runtime']
        collect = vsObj['collect'];
        comment = vsObj['comment'];
        praise = vsObj['praise'];
    }
    if(files.exists(commentSettingPath)){
        var csStr = files.read(commentSettingPath);
        log(csStr)
        var csObj = JSON.parse(csStr);
        comments = csObj['comments']
        messages = csObj['messages'];
    }
}

function endWithToastTotal(){
    // toastLog('操作结束，共执行'+i+'个视频')
}

function countIncream(){
    if(collect && praise && comment){
        //收藏+点赞+评论
        i++
    }else if(collect && praise && !comment){
        //收藏+点赞
        i++
    }else if(!collect && praise && comment){
        //点赞+评论
        i++
    }else if(collect && !praise && comment){
        //收藏+评论
        i++
    }else if(collect && !praise && !comment){
        //只收藏
        i++
    }else if(!collect && praise && !comment){
        //只点赞
        i++
    }else if(!collect && !praise && comment){
        //只评论
        i++
    }
}

//关注
function do_follow() {
    click('关注')
}

//滚动
function scrollToNext() {
    var w = device.width
    var h = device.height
    var x = w / 2
    var y1 = h * 3 / 4
    var y2 = h * 1 / 4
    swipe(x, y1, x, y2, 500)
}
//点赞
function do_praise(withComment) {
    var likes = id('com.tencent.mm:id/cne').find()
    var comments = id('com.tencent.mm:id/cnd').find()
    if (!likes.empty()) {
        var ys = -1811621
        var img = images.captureScreen()
        likes.forEach(function (view, i) {
            var rect = view.bounds()
            var x = rect.centerX()
            var y = rect.centerY()
            if (y <= img.height) {
                var color = images.pixel(img, x, y)
                if (!colors.isSimilar(ys, color)) {
                    log(ys,color,'开始点赞')
                    view.parent().click()
                    if(withComment){
                        sleep(500)
                        do_comment(comments[i])
                    }
                }
            }
        })
    }
}
//收藏
function do_collect(withComment) {
    var collectes = id('com.tencent.mm:id/hc9').find()
    var comments = id('com.tencent.mm:id/cnd').find()
    if (!collectes.empty()) {
        var ys = -352965 //黄色
        var img = images.captureScreen()
        collectes.forEach(function (view, i) {
            var rect = view.bounds()
            var x = rect.centerX()
            var y = rect.centerY()
            log(x, y)
            if (y <= img.height) {
                var color = images.pixel(img, x, y)
                if (!colors.isSimilar(ys, color)) {
                    log('开始收藏')
                    view.parent().click()
                    if(withComment){
                        sleep(500)
                        do_comment(comments[i])
                    }
                }
            }
        })
    }
}

//评论
function do_comment(comment) {
    // var comments = ['很喜欢你的视频','666','大赞','欠揍','苍天饶过谁','我中毒了']
    comment.parent().click()
    sleep(1000)
    var i = random(0,comments.lenght)
    input(comments[i])
    sleep(2000)
    click('回复')
    while (!back());
}