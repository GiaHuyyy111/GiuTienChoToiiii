package com.giutien.toiiii;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
public class MainActivity extends Activity {
 private static final int PICK=101,SAVE=102; private WebView w; private ValueCallback<Uri[]> cb; private String backup,name;
 @Override protected void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.rgb(24,24,24));getWindow().setNavigationBarColor(Color.rgb(24,24,24));w=new WebView(this);setContentView(w);WebSettings s=w.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);s.setAllowContentAccess(true);w.setWebViewClient(new WebViewClient());w.addJavascriptInterface(new Bridge(),"Android");w.setWebChromeClient(new WebChromeClient(){@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> c,FileChooserParams p){if(cb!=null)cb.onReceiveValue(null);cb=c;try{Intent i=p.createIntent();i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");startActivityForResult(i,PICK);return true;}catch(Exception e){cb=null;return false;}}});w.loadUrl("file:///android_asset/index.html");}
 public class Bridge{@JavascriptInterface public void saveBackup(String d,String n){backup=d;name=n;runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,name==null?"giu-tien-backup.json":name);startActivityForResult(i,SAVE);});}}
 @Override protected void onActivityResult(int r,int result,Intent data){super.onActivityResult(r,result,data);if(r==PICK){if(cb==null)return;Uri[] out=null;if(result==RESULT_OK&&data!=null&&data.getData()!=null)out=new Uri[]{data.getData()};cb.onReceiveValue(out);cb=null;}else if(r==SAVE){if(result==RESULT_OK&&data!=null&&data.getData()!=null&&backup!=null){try(OutputStream o=getContentResolver().openOutputStream(data.getData())){if(o!=null)o.write(backup.getBytes(StandardCharsets.UTF_8));}catch(Exception ignored){}}backup=null;name=null;}}
 @Override public void onBackPressed(){if(w!=null&&w.canGoBack())w.goBack();else super.onBackPressed();}
}