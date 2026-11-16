package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/relatorios")
public class RelatorioController {
 private final ChecklistService checklists;private final RelatorioService reports;
 public RelatorioController(ChecklistService c,RelatorioService r){checklists=c;reports=r;}
 @GetMapping public List<ChecklistDto> list(@RequestParam(required=false)String tipo){return checklists.list(tipo);}
 @GetMapping("/{id}")public ChecklistDto get(@PathVariable String id){return checklists.get(id);}
 @GetMapping(value="/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
 public ResponseEntity<byte[]> pdf(@PathVariable String id){
   byte[] data=reports.generate(id);
   return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=relatorio-"+id+".pdf")
     .header(HttpHeaders.CACHE_CONTROL,"no-store").contentType(MediaType.APPLICATION_PDF).body(data);
 }
}
