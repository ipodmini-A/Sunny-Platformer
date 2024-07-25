package sunflowersandroses.platformergame;

import sunflowersandroses.platformergame.enums.Emotion;

import java.util.LinkedList;

class DialogueLine {
    public int id;
    private String message;
    private Emotion emotion;
    private LinkedList<DialogueOption> options; // New field for options

    //private String speaker; Don't know if I should enable this
    public DialogueLine(int id, String m, Emotion e) {
        this.id = id;
        message = m;
        emotion = e;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public LinkedList<DialogueOption> getOptions() {
        return options;
    }

    public void setOptions(LinkedList<DialogueOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return ("ID: " + id + " Message: " + message + " Emotion: " + emotion + " Options : " + options.size() );
    }
}
