package cchase.platformergame;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cchase.platformergame.enums.Emotion;
import org.apache.commons.csv.*;

class DialogueLine {
    public int id;
    private String message;
    private Emotion emotion;
    private LinkedList<DialogueOption> options; // New field for options
    //private String speaker; Don't know if I should enable this
    public DialogueLine(int id, String m, Emotion e)
    {
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
        return ("ID: " + id + " Message: " + message + " Emotion: " + emotion);
    }
}
class DialogueOption {
    private String text;
    private int nextDialogueId;

    public DialogueOption(String text, int nextDialogueId) {
        this.text = text;
        this.nextDialogueId = nextDialogueId;
    }

    // Getters and setters

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNextDialogueId() {
        return nextDialogueId;
    }

    public void setNextDialogueId(int nextDialogueId) {
        this.nextDialogueId = nextDialogueId;
    }
}


public class Dialogue {
    public static List<DialogueLine> loadDialogue(String filePath) {
        List<DialogueLine> dialogueLines = new ArrayList<>();

        try (
                FileReader reader = new FileReader(filePath);
                CSVParser csvParser = CSVFormat.Builder.create()
                        .setHeader("id", "message", "emotion","options")
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
                DialogueLine dialogueLine = new DialogueLine(id, message, emotion);

                // Parse options if they exist
                try {
                    String optionsStr = csvRecord.get("options");
                    if (optionsStr != null && !optionsStr.isEmpty()) {
                        LinkedList<DialogueOption> options = new LinkedList<>();
                        String[] optionsArray = optionsStr.split(";");
                        for (String optionStr : optionsArray) {
                            String[] parts = optionStr.split(":");
                            String optionText = parts[0].trim();
                            int nextDialogueId = Integer.parseInt(parts[1].trim());
                            options.add(new DialogueOption(optionText, nextDialogueId));
                            dialogueLine.setOptions(options);
                        }
                    }
                } catch (Exception e)
                {
                    emotion = Emotion.NEUTRAL;
                }
                dialogueLines.add(dialogueLine);
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
                if (PlatformerGame.dialogueLines.get(j).getOptions() == null) {
                    list.add(new dialogueString(PlatformerGame.dialogueLines.get(j).getMessage(), PlatformerGame.dialogueLines.get(j).getEmotion()));
                } else
                {
                    list.add(new dialogueString(PlatformerGame.dialogueLines.get(j).getMessage(), PlatformerGame.dialogueLines.get(j).getEmotion(), PlatformerGame.dialogueLines.get(j).getOptions()));
                }
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
        private LinkedList<DialogueOption> options; // New field for options
        public dialogueString(String s, Emotion e)
        {
            message = s;
            emotion = e;
        }

        public dialogueString(String s, Emotion e, LinkedList<DialogueOption> o)
        {
            message = s;
            emotion = e;
            this.options = o;
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

        public LinkedList<DialogueOption> getOptions() {
            return options;
        }

        public void setOptions(LinkedList<DialogueOption> options) {
            this.options = options;
        }
    }
}
