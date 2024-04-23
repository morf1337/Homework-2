package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.AuthorDAO;
import org.example.dto.AuthorDto;
import org.example.entity.Author;
import org.example.mapper.AuthorMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/authors")
public class AuthorServlet extends HttpServlet {
    private final AuthorDAO authorDAO;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            Long id = Long.parseLong(idParam);
            Author author = authorDAO.getAuthorById(id);
            if (author != null) {
                AuthorDto authorDto = AuthorMapper.INSTANCE.authorToAuthorDto(author);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(authorDto));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            List<Author> authors = authorDAO.getAllAuthors();
            List<AuthorDto> authorDtos = authors.stream()
                    .map(AuthorMapper.INSTANCE::authorToAuthorDto)
                    .collect(Collectors.toList());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(authorDtos));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name != null && !name.isEmpty()) {
            Author author = new Author();
            author.setName(name);
            authorDAO.addAuthor(author);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        if (idParam != null && name != null && !idParam.isEmpty() && !name.isEmpty()) {
            Long id = Long.parseLong(idParam);
            Author existingAuthor = authorDAO.getAuthorById(id);
            if (existingAuthor != null) {
                existingAuthor.setName(name);
                authorDAO.updateAuthor(existingAuthor);
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
            AuthorDAO AuthorDAO;
            Author existingAuthor = authorDAO.getAuthorById(id);
            if (existingAuthor != null) {
                authorDAO.deleteAuthor(existingAuthor.getId());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public AuthorServlet() {
        authorDAO = new AuthorDAO();
    }

    public AuthorServlet(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }
}

