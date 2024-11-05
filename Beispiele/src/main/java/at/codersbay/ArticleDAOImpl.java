package at.codersbay;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ArticleDAOImpl implements ArticleDAO {
    @Override
    public int createArticle(Article article) throws RuntimeException {
        String query = "INSERT INTO ARTICLES (NAME, OWNERID) VALUES (?, ?)";
        int autoIncKeyFromApi = -1;

        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, article.getName());
            stmt.setInt(2, article.getOwnerId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1); // Jede generierte ID auslesen. In diesem Beispiel wurde nur ein Datenset erstellt.
            } else {
                throw new SQLException("Beim INSERT wurde keine Autoincrement-ID generiert");
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return autoIncKeyFromApi;
    }

    @Override
    public int updateArticle(Article article) throws RuntimeException {
        int affectedRows = -1;
        String query = "update ARTICLES set NAME = ?, OWNERID = ? where ID = ?";
        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)){

            stmt.setString(1, article.getName());
            stmt.setInt(2, article.getOwnerId());
            stmt.setInt(3, article.getId());
            affectedRows = stmt.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return affectedRows;
    }

    @Override
    public int deleteArticle(int articleId) throws RuntimeException {
        int affectedRows = -1;
        int autoIncKeyFromApi = -1;
        String query = "DELETE FROM ARTICLES WHERE ID = ?";

        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, articleId);

            affectedRows = stmt.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return affectedRows;
    }

    @Override
    public Article getArticle(int articleId) throws RuntimeException {
        Article article = null;
        String query = "SELECT ID, NAME, OWNERID FROM ARTICLES WHERE ID = ?";
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                article = new Article(rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getInt("OWNERID"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return article;
    }

    @Override
    public ArrayList<Article> getAllArticles() throws RuntimeException {

        ArrayList<Article> articles = new ArrayList<>();
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT ID, FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT FROM CLIENTS")) { // Definition des Statements mit Platzhaltern: '?'
            ResultSet rs = ps.executeQuery(); // Absetzen der Query


            // Hier wird das Ergebnis der Abfrage verarbeitet
            while (rs.next()) { // Zugriff auf die nächste Zeile. Wird benötigt, auch wenn das Ergebnis nur 1 Zeile hat!

                Article article = new Article(rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getInt("OWNERID"));
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return articles;
    }
}
