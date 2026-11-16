package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.ResumoDashboard;
import com.elevatecontrol.backend.service.DashboardService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
 private final DashboardService service;
 public DashboardController(DashboardService s){service=s;}
 @GetMapping("/resumo")public ResumoDashboard get(){return service.get();}
}
