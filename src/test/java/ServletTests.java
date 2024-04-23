import org.example.dao.AuthorDAO;
import org.example.dao.BookDAO;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.servlet.AuthorServlet;
import org.example.servlet.BookServlet;
import org.example.servlet.ContextListener;
import org.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Testcontainers
public class ServletTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    private Connection testConnection;
    private MockedStatic<DatabaseConnection> dbconn;

    @BeforeEach
    void beforeEach() throws SQLException {
        postgres.start();
        testConnection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        dbconn = Mockito.mockStatic(DatabaseConnection.class);
        dbconn.when(DatabaseConnection::getConnection).thenReturn(testConnection);
        ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
        ContextListener listener = new ContextListener();
        listener.contextInitialized(sce);
        listener.contextDestroyed(sce);
    }

    @AfterEach
    void afterEach() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
        postgres.stop();
        dbconn.close();
    }

    @Test
    public void testDoGetAllAuthors() throws Exception {
        AuthorServlet testServlet = new AuthorServlet();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = Mockito.mock(PrintWriter.class);

        when(request.getParameter("id")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        String expectedJson = "[{\"id\":1,\"name\":\"George Orwell\"},{\"id\":2,\"name\":\"J.K. Rowling\"},{\"id\":3,\"name\":\"Harper Lee\"}]";
        testServlet.doGet(request, response);
        Mockito.verify(writer).write(expectedJson);
    }

    @Test
    public void testDoGetAuthorById() throws Exception {
        AuthorServlet testServlet = new AuthorServlet();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = Mockito.mock(PrintWriter.class);

        when(request.getParameter("id")).thenReturn("3");
        when(response.getWriter()).thenReturn(writer);

        String expectedJson = "{\"id\":3,\"name\":\"Harper Lee\"}";
        testServlet.doGet(request, response);
        Mockito.verify(writer).write(expectedJson);
    }

    @Test
    public void testDoPostAddAuthor() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("name")).thenReturn("Test Author");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doPost(request, response);

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        Mockito.verify(authorDAO).addAuthor(authorCaptor.capture());
        Author capturedAuthor = authorCaptor.getValue();
        assertEquals("Test Author", capturedAuthor.getName());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoPostAuthorWithEmptyName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("name")).thenReturn("");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPutAuthorWithEmptyId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("id")).thenReturn("");
        when(request.getParameter("name")).thenReturn("Updated Author");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPutAuthorWithEmptyName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDeleteAuthorWithEmptyId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("id")).thenReturn("");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doDelete(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPostAddAuthorWithValidName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        when(request.getParameter("name")).thenReturn("Test Author");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doPost(request, response);

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        Mockito.verify(authorDAO).addAuthor(authorCaptor.capture());
        Author capturedAuthor = authorCaptor.getValue();
        assertEquals("Test Author", capturedAuthor.getName());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoDeleteAuthorWithValidId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthorDAO authorDAO = Mockito.mock(AuthorDAO.class);
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        when(authorDAO.getAuthorById(1L)).thenReturn(existingAuthor);

        when(request.getParameter("id")).thenReturn("1");
        AuthorServlet authorServlet = new AuthorServlet(authorDAO);

        authorServlet.doDelete(request, response);

        Mockito.verify(authorDAO).deleteAuthor(1L);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetAllBooks() throws Exception {
        BookServlet testServlet = new BookServlet();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = Mockito.mock(PrintWriter.class);

        when(request.getParameter("id")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        String expectedJson = "[{\"id\":1,\"title\":\"1984\"},{\"id\":2,\"title\":\"Harry Potter and the Philosopher's Stone\"},{\"id\":3,\"title\":\"To Kill a Mockingbird\"}]";
        testServlet.doGet(request, response);
        Mockito.verify(writer).write(expectedJson);
    }

    @Test
    public void testDoGetBookById() throws Exception {
        BookServlet testServlet = new BookServlet();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = Mockito.mock(PrintWriter.class);

        when(request.getParameter("id")).thenReturn("2");
        when(response.getWriter()).thenReturn(writer);

        String expectedJson = "{\"id\":2,\"title\":\"Harry Potter and the Philosopher's Stone\"}";
        testServlet.doGet(request, response);
        Mockito.verify(writer).write(expectedJson);
    }

    @Test
    public void testDoPostAddBook() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("title")).thenReturn("Test Book");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doPost(request, response);

        ArgumentCaptor<Book> bookArgumentCaptorCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookDAO).addBook(bookArgumentCaptorCaptor.capture());
        Book capturedBook = bookArgumentCaptorCaptor.getValue();
        assertEquals("Test Book", capturedBook.getTitle());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoPostBookWithEmptyName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("title")).thenReturn("");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPutBookWithEmptyId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("id")).thenReturn("");
        when(request.getParameter("title")).thenReturn("Updated Book");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPutBookWithEmptyName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doPut(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDeleteBookWithEmptyId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("id")).thenReturn("");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doDelete(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPostAddBookWithValidName() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        when(request.getParameter("title")).thenReturn("Test Book");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doPost(request, response);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookDAO).addBook(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertEquals("Test Book", capturedBook.getTitle());
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    public void testDoDeleteBookWithValidId() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        BookDAO bookDAO = Mockito.mock(BookDAO.class);
        Book existingBook = new Book();
        existingBook.setId(1L);
        when(bookDAO.getBookById(1L)).thenReturn(existingBook);

        when(request.getParameter("id")).thenReturn("1");
        BookServlet bookServlet = new BookServlet(bookDAO);

        bookServlet.doDelete(request, response);

        Mockito.verify(bookDAO).deleteBook(1L);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
