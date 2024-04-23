package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.BookDAO;
import org.example.dto.BookDto;
import org.example.entity.Book;
import org.example.mapper.BookMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    private final BookDAO bookDAO;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            Long id = Long.parseLong(idParam);
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                BookDto bookDto = BookMapper.INSTANCE.bookToBookDto(book);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(bookDto));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            List<Book> books = bookDAO.getAllBooks();
            List<BookDto> bookDtos = books.stream()
                    .map(BookMapper.INSTANCE::bookToBookDto)
                    .collect(Collectors.toList());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(bookDtos));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        if (title != null && !title.isEmpty()) {
            Book book = new Book();
            book.setTitle(title);
            bookDAO.addBook(book);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        if (idParam != null && title != null && !idParam.isEmpty() && !title.isEmpty()) {
            Long id = Long.parseLong(idParam);
            Book existingBook = bookDAO.getBookById(id);
            if (existingBook != null) {
                existingBook.setTitle(title);
                bookDAO.updateBook(existingBook);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            Long id = Long.parseLong(idParam);
            Book existingBook = bookDAO.getBookById(id);
            if (existingBook != null) {
                bookDAO.deleteBook(existingBook.getId());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public BookServlet() {
        bookDAO = new BookDAO();
    }

    public BookServlet(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }
}
