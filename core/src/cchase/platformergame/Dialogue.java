package cchase.platformergame;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.csv.*;

class DialogueLine {
    public int id;
    private String message;
    private Emotion emotion;
    //private String speaker; Don't know if I should enable this
    public DialogueLine(int id, String m, Emotion e)
    {
        this.id = id;
        message = m;
        emotion = e;
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
}

public class Dialogue {
    public static List<DialogueLine> loadDialogue(String filePath) {
        List<DialogueLine> dialogueLines = new ArrayList<>();

        try (
                FileReader reader = new FileReader(filePath);
                CSVParser csvParser = CSVFormat.Builder.create()
                        .setHeader("id", "message", "emotion")
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                int id = Integer.parseInt(csvRecord.get("id"));
                String message = csvRecord.get("message");
                Emotion emotion;
                try {
                    emotion = Emotion.valueOf(csvRecord.get("emotion"));
                } catch (Exception e)
                {
                    emotion = Emotion.NEUTRAL;
                }
                System.out.println(emotion);
                dialogueLines.add(new DialogueLine(id, message, emotion));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialogueLines;
    }

    public static LinkedList<dialogueString> getMessageGroup(int i)
    {
        LinkedList<dialogueString> list = new LinkedList<>();
        for (int j = 0; j < PlatformerGame.dialogueLines.size(); j++)
        {
            if (PlatformerGame.dialogueLines.get(j).id == i)
            {
                list.add(new dialogueString(PlatformerGame.dialogueLines.get(j).getMessage(), PlatformerGame.dialogueLines.get(j).getEmotion()));
            }
        }
        return list;
    }

    /**
     * This class is dedicated to storing data for both Strings and Emotions. This is important for the portraits
     * that are displayed in the overworld
     * TODO: Figure out a way to make creating a new object not painful
     */
    public static class dialogueString
    {
        private String message;
        private Emotion emotion;
        public dialogueString(String s, Emotion e)
        {
            message = s;
            emotion = e;
        }

        public dialogueString(String s)
        {
            message = s;
            emotion = Emotion.NEUTRAL;
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
    }
}
