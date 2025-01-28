package models;

public class Request {
    private String text;
    private String voiceId;  // (e.g. "Joanna", "Matthew", "Kimberley"

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }
}
