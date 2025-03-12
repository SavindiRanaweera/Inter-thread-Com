package lk.ijse.dep13.interthreadcommunications.listener;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lk.ijse.dep13.interthreadcommunications.db.MontisoriCP;

import java.util.Set;

public class AppListener implements ServletContainerInitializer {
    @Override
    public void onStartup ( Set<Class<?>> set, ServletContext servletContext ) throws ServletException {
        MontisoriCP montisoriCP = new MontisoriCP();
        servletContext.setAttribute("datasource", montisoriCP);
    }
}
