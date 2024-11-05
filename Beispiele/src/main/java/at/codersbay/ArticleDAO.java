package at.codersbay;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ArticleDAO {
    public int createArticle(Article article) throws RuntimeException;
    public int updateArticle(Article article) throws RuntimeException;
    public int deleteArticle(int articleId) throws RuntimeException;
    public Article getArticle(int articleId) throws RuntimeException;
    public ArrayList<Article> getAllArticles() throws RuntimeException;
}
