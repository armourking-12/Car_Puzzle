var integration = {}
integration.hideBanner = function() {
    if (!integration.ads) {
        try {
            Android.hideAndroidBanner()
        } catch (e) {
            console.log(e)
        }
    }
}
integration.showBanner = function() {
    if (!integration.ads) {
        try {
            Android.showAndroidBanner()
        } catch (e) {
            console.log(e)
        }
    } else {}
}
integration.showInterstitial = function() {
    if (!integration.ads) {
        try {
            Android.showAd()
        } catch (e) {
            console.log(e)
        }
    } else {}
}
integration.showRewardedAd = function() {
    if (!integration.ads) {
        try {
            Android.showRewardedAd();
        } catch (e) {
            integration.rewarded();
        }
    } else {
        integration.rewarded();
    }
}
integration.rewarded = function() {
    window.pendingReward = true;
};
integration.adClosed = function() {
    console.log("pendingReward:", window.pendingReward);
    console.log("pendingSkill:", window.pendingSkill);
    console.log("mainLayer:", window.mainLayer);
}

integration.landscape = function (){
    try{
        Android.landscape()
    }catch(e){
        console.log(e)
    }
}

integration.ads = false
integration.itsads = function(n){
    if(n == 1){
        integration.hideBanner()
        integration.ads = true
    }else{
        integration.ads = false
    }
}