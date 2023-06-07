public class TriviaQuestion {
    private String category;
    private String question;
    private boolean correctAnswer;
    private String difficulty;

    public TriviaQuestion(String category, String question, boolean correctAnswer, String difficulty) {
        this.category = category;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public String getDifficulty() {
        return difficulty;
    }
}