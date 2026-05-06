package com.group13.reportsystem.controller;

import com.group13.reportsystem.service.PortalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    private final PortalService portalService;

    public HomeController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/")
    public String home(@RequestParam(name = "selectedReport", required = false) Integer selectedReport,
                       Model model,
                       HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            model.addAllAttributes(portalService.buildLoginView());
            return "index";
        }

        var currentUser = portalService.getUserById(currentUserId);
        if (currentUser == null) {
            session.invalidate();
            model.addAllAttributes(portalService.buildLoginView());
            return "index";
        }

        model.addAllAttributes(portalService.buildDashboardData(currentUser, selectedReport));
        return "index";
    }
}
