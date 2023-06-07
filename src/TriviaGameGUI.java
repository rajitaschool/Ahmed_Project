import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TriviaGameGUI extends JFrame {
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;

    private static final String API_URL = "https://opentdb.com/api.php?amount=10&difficulty=%s&type=boolean&token=89dce38fb69bf2a38df261b841845706dc5c15a51c9e65bf37a972ce6812e33c";

    private static final HashMap<String, String> htmlEntities;

    static {
        htmlEntities = new HashMap<String, String>();
        htmlEntities.put("&lt;", "<");
        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");
        htmlEntities.put("&#039;", "'");
        htmlEntities.put("&rsquo;", "'");
    }

    public static final String unescapeHTML(String source) {
        int i, j;

        boolean continueLoop;
        int skip = 0;
        do {
            continueLoop = false;
            i = source.indexOf("&", skip);
            if (i > -1) {
                j = source.indexOf(";", i);
                if (j > i) {
                    String entityToLookFor = source.substring(i, j + 1);
                    String value = (String) htmlEntities.get(entityToLookFor);
                    if (value != null) {
                        source = source.substring(0, i)
                                + value + source.substring(j + 1);
                        continueLoop = true;
                    } else if (value == null) {
                        skip = i + 1;
                        continueLoop = true;
                    }
                }
            }
        } while (continueLoop);
        return source;
    }

    public TriviaGameGUI() {
        super("Trivia Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to Trivia Quiz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        easyButton = new JButton("Easy");
        easyButton.addActionListener(new DifficultyButtonListener());
        buttonPanel.add(easyButton);
        mediumButton = new JButton("Medium");
        mediumButton.addActionListener(new DifficultyButtonListener());
        buttonPanel.add(mediumButton);
        hardButton = new JButton("Hard");
        hardButton.addActionListener(new DifficultyButtonListener());
        buttonPanel.add(hardButton);
        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private class DifficultyButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String difficulty = "";
            if (e.getSource() == easyButton) {
                difficulty = "easy";
            } else if (e.getSource() == mediumButton) {
                difficulty = "medium";
            } else if (e.getSource() == hardButton) {
                difficulty = "hard";
            }

            fetchTriviaQuestions(difficulty);
        }
    }

    private void fetchTriviaQuestions(String difficulty) {
        String url = String.format(API_URL, difficulty);
        String urlResponse = "";

        try {
            URI myUri = URI.create(url);
            HttpRequest request = HttpRequest.newBuilder().uri(myUri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            urlResponse = response.body();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JSONObject jsonObject = new JSONObject(urlResponse);
        JSONArray questionArray = jsonObject.getJSONArray("results");

        List<TriviaQuestion> triviaQuestions = new ArrayList<>();
        for (int i = 0; i < questionArray.length(); i++) {
            JSONObject questionObject = questionArray.getJSONObject(i);
            String category = unescapeHTML(questionObject.getString("category"));
            String questionText = unescapeHTML(questionObject.getString("question"));
            boolean correctAnswer = questionObject.getBoolean("correct_answer");
            String difficultyLevel = difficulty;

            TriviaQuestion question = new TriviaQuestion(category, questionText, correctAnswer, difficultyLevel);
            triviaQuestions.add(question);
        }

        // Launch Trivia Quiz GUI
        TriviaQuizGUI quizGUI = new TriviaQuizGUI(triviaQuestions);
        quizGUI.setVisible(true);
        setVisible(false);
    }
}