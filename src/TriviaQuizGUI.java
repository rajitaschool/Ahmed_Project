import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONObject;

public class TriviaQuizGUI extends JFrame {
    private List<TriviaQuestion> triviaQuestions;
    private int currentQuestionIndex;
    private int totalScore;

    private JLabel categoryLabel;
    private JLabel questionLabel;
    private JButton trueButton;
    private JButton falseButton;
    private JLabel resultLabel;
    private JButton continueButton;
    private JLabel questionNumberLabel;
    private JLabel scoreLabel;
    private JButton quitButton;
    private JButton retryButton;

    public TriviaQuizGUI(List<TriviaQuestion> triviaQuestions) {
        super("Trivia Quiz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        this.triviaQuestions = triviaQuestions;
        this.currentQuestionIndex = 0;
        this.totalScore = 0;

        categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(categoryLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 1));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        questionNumberLabel = new JLabel("Question " + (currentQuestionIndex + 1) + " of 10");
        scoreLabel = new JLabel("Score: " + totalScore);
        infoPanel.add(questionNumberLabel);
        infoPanel.add(scoreLabel);
        mainPanel.add(infoPanel);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(questionLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        trueButton = new JButton("True");
        trueButton.addActionListener(new AnswerButtonListener(true));
        buttonPanel.add(trueButton);

        falseButton = new JButton("False");
        falseButton.addActionListener(new AnswerButtonListener(false));
        buttonPanel.add(falseButton);

        mainPanel.add(buttonPanel);

        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(resultLabel);

        continueButton = new JButton("Continue");
        continueButton.addActionListener(new ContinueButtonListener());
        continueButton.setVisible(false);
        mainPanel.add(continueButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitButtonListener());
        quitButton.setVisible(false);
        bottomPanel.add(quitButton);

        retryButton = new JButton("Retry");
        retryButton.addActionListener(new RetryButtonListener());
        retryButton.setVisible(false);
        bottomPanel.add(retryButton);

        mainPanel.add(bottomPanel);

        add(mainPanel, BorderLayout.CENTER);

        displayQuestion();
        pack();
        setLocationRelativeTo(null);
    }

    private void displayQuestion() {
        if (currentQuestionIndex < triviaQuestions.size()) {
            TriviaQuestion question = triviaQuestions.get(currentQuestionIndex);
            categoryLabel.setText("Category: " + question.getCategory());
            questionLabel.setText("<html><center>" + question.getQuestion() + "</center></html>");
            trueButton.setEnabled(true);
            falseButton.setEnabled(true);
            resultLabel.setText("");
            continueButton.setVisible(false);
            questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of 10");
            scoreLabel.setText("Score: " + totalScore);
            quitButton.setVisible(false);
            retryButton.setVisible(false);
        } else {
            showFinalScore();
        }
    }

    private void showFinalScore() {
        questionLabel.setText("Quiz Finished!");
        categoryLabel.setText("");
        trueButton.setVisible(false);
        falseButton.setVisible(false);
        resultLabel.setText("Your Total Score: " + totalScore);
        continueButton.setVisible(false);
        quitButton.setVisible(true);
        retryButton.setVisible(true);
    }

    private class AnswerButtonListener implements ActionListener {
        private boolean selectedAnswer;

        public AnswerButtonListener(boolean selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }

        public void actionPerformed(ActionEvent e) {
            TriviaQuestion question = triviaQuestions.get(currentQuestionIndex);
            boolean correctAnswer = question.isCorrectAnswer();

            if (selectedAnswer == correctAnswer) {
                resultLabel.setText("Correct!");
                totalScore += (question.getDifficulty().equals("easy") ? 0.5 :
                        question.getDifficulty().equals("medium") ? 1 : 2) * 100;
                fetchJokeAndDisplay();
            } else {
                resultLabel.setText("Incorrect!");
                continueButton.setVisible(true);
                quitButton.setVisible(false);
                retryButton.setVisible(false);
            }

            trueButton.setEnabled(false);
            falseButton.setEnabled(false);
        }
    }

    private class ContinueButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            currentQuestionIndex++;
            displayQuestion();
        }
    }

    private class QuitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class RetryButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TriviaGameGUI newQuiz = new TriviaGameGUI();
            newQuiz.setVisible(true);
            dispose();
        }
    }

    private void fetchJokeAndDisplay() {
        try {
            URI myUri = URI.create("https://icanhazdadjoke.com/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(myUri)
                    .header("Accept", "application/json")
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            JSONObject jsonObject = new JSONObject(jsonResponse);
            String joke = jsonObject.getString("joke");

            resultLabel.setText("<html><center>Correct!<br>Here's a joke for you:<br>" + joke + "</center></html>");
            continueButton.setVisible(true);
            quitButton.setVisible(false);
            retryButton.setVisible(false);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}