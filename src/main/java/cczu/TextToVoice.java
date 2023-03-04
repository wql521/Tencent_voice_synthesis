package cczu;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tts.v20190823.TtsClient;
import com.tencentcloudapi.tts.v20190823.models.TextToVoiceRequest;
import com.tencentcloudapi.tts.v20190823.models.TextToVoiceResponse;

import javax.sound.sampled.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class TextToVoice {
    public static String Path = null;
    private static final String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();

    private TextToVoice(){}

    public static void Text_Voice(String text) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (Path != null){
            try{
                // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
                // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。
                Credential cred = new Credential("SecretId", "SecretKey");
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("tts.ap-shanghai.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                // 实例化要请求产品的client对象,clientProfile是可选的
                TtsClient client = new TtsClient(cred, "ap-shanghai", clientProfile);
                // 实例化一个请求对象,每个接口都会对应一个request对象
                TextToVoiceRequest req = new TextToVoiceRequest();
                req.setText(text);
                req.setSessionId(uuid);
                req.setVolume(0F);
                req.setSpeed(0F);
                req.setProjectId(0L);
                req.setModelType(1L);
                req.setVoiceType(1002L);
                req.setPrimaryLanguage(1L);
                req.setCodec("wav");
                req.setEnableSubtitle(true);
                req.setSegmentRate(0L);
                // 返回的resp是一个TextToVoiceResponse的实例，与请求对象对应
                TextToVoiceResponse resp = client.TextToVoice(req);
                // 获取返回的语音字符数组
                String audio = resp.getAudio();
                // 转换字符数组
                convertByteStrStrToVoice(audio,Path,uuid+".wav");
                WavPlay(Path+uuid+".wav");
                // 输出json格式的字符串回包
                //System.out.println(TextToVoiceResponse.toJsonString(resp));
            } catch (TencentCloudSDKException e) {
                System.out.println(e);
            }
        }else {
            System.out.println("Path的路径为空，请先设置路径。");
        }
    }

    private static void convertByteStrStrToVoice(String byteArrStr, String path, String fileName){

        File judgeExists = new File(path);
        if (!judgeExists.exists()){
            // 如果路径不存在就创建
            boolean mkdirs = judgeExists.mkdirs();
            if (mkdirs){
                System.out.println("创建成功！");
            }
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File newFile = new File(path + fileName);
        try {
            if (!newFile.exists()) {
                boolean newFile1 = newFile.createNewFile();
                if (newFile1){
                    System.out.println(fileName+"创建成功！");
                }
            }
            byte[] bytes = Base64.getDecoder().decode(byteArrStr);
            fos = new java.io.FileOutputStream(newFile);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            System.out.println("语音报存到本地出错，请检查:" + e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    System.out.println("语音报存到本地出错，请检查:" + e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("语音报存到本地出错，请检查:" + e.getMessage());
                }
            }
        }
    }


    private static void WavPlay(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        int count;
        byte[] buf = new byte[1024];
        //获取音频输入流
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
        //获取音频的编码格式
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,format,AudioSystem.NOT_SPECIFIED);
        SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(format);
        sourceDataLine.start();
        //播放音频
        while((count = audioInputStream.read(buf,0,buf.length)) != -1){
            sourceDataLine.write(buf,0,count);
        }
        //播放结束，释放资源
        sourceDataLine.drain();
        sourceDataLine.close();
        audioInputStream.close();
    }
}
