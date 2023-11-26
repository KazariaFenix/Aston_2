package ru.marzuev;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import ru.marzuev.service.AuthorService;
import ru.marzuev.service.impl.AuthorServiceImpl;
import ru.marzuev.servlet.AuthorServlet;
import ru.marzuev.servlet.BookServlet;
import ru.marzuev.servlet.CommentServlet;

public class ApplicationMain {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.getConnector().setPort(8080);
        Context tomcatContext = tomcat.addContext("", null);
        Wrapper authorServletWrapper =
                Tomcat.addServlet(tomcatContext, "authorsServlet", new AuthorServlet());
        Wrapper bookServletWrapper =
                Tomcat.addServlet(tomcatContext, "booksServlet", new BookServlet());
        Wrapper commentServletWrapper =
                Tomcat.addServlet(tomcatContext, "commentsServlet", new CommentServlet());
        authorServletWrapper.addMapping("/authors/*");
        bookServletWrapper.addMapping("/books/*");
        commentServletWrapper.addMapping("/comments/*");
        tomcat.start();
    }
}
