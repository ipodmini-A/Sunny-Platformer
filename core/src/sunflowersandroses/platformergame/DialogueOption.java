package sunflowersandroses.platformergame;

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
