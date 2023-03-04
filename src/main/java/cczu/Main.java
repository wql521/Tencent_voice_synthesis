package cczu;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        TextToVoice.Path="/Users/wangqianlong/Desktop/All/IntelliJ IDEA/语言合成_wav/";
        TextToVoice.Text_Voice("我喜欢她");
    }
}