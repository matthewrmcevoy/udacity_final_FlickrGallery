package com.mrm.android.flikrtest.oauth

object OAuthConstants {
    const val API_KEY = "64187751b962051b4eb86e0c23bd7874"
    const val API_SECRET = "d3cc409f4d513aea&"
    const val REQUEST_BASE = "https://www.flickr.com/services/oauth/request_token"
    const val UPLOAD_BASE = "https://up.flickr.com/services/upload"
    const val OAUTH_SIG_METHOD = "HMAC-SHA1"
    const val OAUTH_VERSION = "1.0"
    val OAUTH_TOKEN = CurrentUser.oa_token
    val OAUTH_TOKEN_SECRET = CurrentUser.oa_token_secret
}