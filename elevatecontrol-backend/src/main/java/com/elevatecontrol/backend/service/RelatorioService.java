package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.support.*;
import java.io.*;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.domain.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
@Service
public class RelatorioService {
 private final ChecklistService checklists;private final AuditService audit;
 private final UsuarioRepository users;private final ChamadoRepository tickets;
 private final PredioRepository buildings;private final ElevadorRepository elevators;
 public RelatorioService(ChecklistService c,AuditService a,UsuarioRepository u,ChamadoRepository t,PredioRepository b,ElevadorRepository e){
   checklists=c;audit=a;users=u;tickets=t;buildings=b;elevators=e;
 }
 public byte[] generate(String checklistId){
   ChecklistDto chk=checklists.get(checklistId); // consulta aplica as permissoes de perfil
   try(PDDocument doc=new PDDocument();ByteArrayOutputStream output=new ByteArrayOutputStream()){
     PageWriter pdf=new PageWriter(doc);
     pdf.line("RELATORIO TECNICO DE ATENDIMENTO",true);
     pdf.line("Checklist: "+chk.id(),false);
     pdf.line("Chamado: "+chk.chamadoId(),false);
     var chamado=tickets.findById(chk.chamadoId()).orElse(null);
     if(chamado!=null){
       var predio=buildings.findById(chamado.getPredioId()).orElse(null);
       pdf.line("Solicitacao: "+chamado.getTitulo(),false);
       if(predio!=null){pdf.line("Predio: "+predio.getNome(),false);pdf.line("Localizacao: "+predio.getEndereco(),false);}
     }
     pdf.line("Tipo: "+chk.tipo()+" | Tecnico: "+users.findById(Objects.toString(chk.tecnicoId(),""))
       .map(Usuario::getNome).orElse(Objects.toString(chk.tecnicoId(),"Nao identificado")),false);
     pdf.line("Entrada: "+dateBr(chk.inicio())+" | Saida: "+dateBr(chk.fim()),false);
     pdf.line("Responsavel: "+chk.responsavel(),false);
     pdf.line("Pecas substituidas: "+orEmpty(chk.pecas()),false);
     pdf.line("Servico executado: "+orEmpty(chk.observacoes()),false);
     pdf.line("Lubrificacao: "+(chk.lubrificacao()?"Sim":"Nao")+" | Teste seguranca: "+(chk.testeSeguranca()?"Sim":"Nao"),false);
     RegistroAtendimento reg=chk.registroAtendimento();
     if(reg!=null){
       pdf.space(9);pdf.line("REGISTRO DE ATENDIMENTO - "+reg.numeroRegistro(),true);
       pdf.line("Ocorrencia: "+orEmpty(reg.ocorrencia()),false);
       pdf.line("Observacoes gerais: "+orEmpty(reg.observacoesEmpresa()),false);
       pdf.line("Materiais necessarios: "+orEmpty(reg.materialNecessario()),false);
       if(reg.equipamentos()!=null)for(var eq:reg.equipamentos()){
         pdf.space(8);pdf.line("EQUIPAMENTO: "+elevators.findById(eq.elevadorId())
           .map(e->e.getIdentificacao()+" | "+e.getMarca()+" "+e.getModelo()).orElse(eq.elevadorId()),true);
         pdf.line("Tipo de visita: "+String.join(", ",orList(eq.tiposVisita())),false);
         pdf.line("Situacao: "+String.join(", ",orList(eq.situacoes())),false);
         if(eq.outros()!=null&&!eq.outros().isBlank())pdf.line("Outros: "+eq.outros(),false);
         if(eq.checklistMensal()!=null){
           var monthly=eq.checklistMensal();pdf.line("CHECKLIST MENSAL 2026 - 29 ROTINAS",true);
           Map<String,String> answers=new HashMap<>();monthly.respostas().forEach(x->answers.put(x.codigo(),x.resposta()));
           for(var item:MonthlyCatalog.ROTINAS)pdf.line(item.codigo()+" ["+answers.getOrDefault(item.codigo(),"-")+"] "+item.descricao(),false);
           pdf.line("LL: "+orEmpty(monthly.ll()),false);
           pdf.line("Folga redutor (mm): "+orEmpty(monthly.folgaRedutorMm()),false);
           pdf.line("Acrilicos: "+orEmpty(monthly.medidasAcrilicos()),false);
           pdf.line("Distancia contrapeso/parachoque (mm): "+orEmpty(monthly.distanciaContrapesoParachoqueMm()),false);
         }
       }
       if(reg.autorizacaoMaterial()!=null){
         pdf.space(8);pdf.line("AUTORIZACAO DE MATERIAL",true);
         pdf.line("Data: "+reg.autorizacaoMaterial().data()+" | Cliente: "+reg.autorizacaoMaterial().nomeCliente(),false);
         pdf.image("Assinatura de autorizacao",reg.autorizacaoMaterial().assinaturaCliente());
       }
       if(reg.servicoExecutado()==null){
         pdf.space(8);pdf.line("VALIDACAO PENDENTE: servico nao aprovado pelo responsavel.",true);
       }
       if(reg.servicoExecutado()!=null){
         pdf.space(8);pdf.line("VALIDACAO DO SERVICO",true);
         pdf.line("Cliente: "+reg.servicoExecutado().nomeCliente()+" | Tecnico: "+reg.servicoExecutado().tecnico(),false);
         pdf.image("Assinatura do condominio",reg.servicoExecutado().assinaturaCliente());
       }
       if(reg.assinaturasMensais()!=null){
         pdf.space(8);pdf.line("ASSINATURAS MANUTENCAO MENSAL",true);
         pdf.image("Assinatura do tecnico",reg.assinaturasMensais().assinaturaTecnico());
         if(reg.assinaturasMensais().assinaturaLiderServicos()!=null&&!reg.assinaturasMensais().assinaturaLiderServicos().isBlank()){
           pdf.line("Lider: "+reg.assinaturasMensais().nomeLiderServicos(),false);
           pdf.image("Assinatura do lider",reg.assinaturasMensais().assinaturaLiderServicos());
         }
       }
     }
     pdf.close();doc.save(output);audit.record("DOWNLOAD_PDF","Checklist",checklistId);return output.toByteArray();
   }catch(IOException ex){throw new IllegalStateException("Falha ao gerar PDF",ex);}
 }
 private static String dateBr(String raw){
   if(raw==null||raw.isBlank())return "Nao informado";
   try{return LocalDateTime.parse(raw).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));}
   catch(Exception ex){return raw;}
 }
 private static String orEmpty(String s){return s==null||s.isBlank()?"Nao informado":s;}
 private static <T>List<T> orList(List<T> l){return l==null?List.of():l;}
 private static class PageWriter {
    private final PDDocument doc;private PDPageContentStream stream;private float y;private int pageNumber;
    private final PDFont regular=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final Color BLUE=new Color(26,64,97);
    private static final Color SECONDARY=new Color(65,87,107);
    private static final Color LIGHT=new Color(218,229,239);
    PageWriter(PDDocument d)throws IOException{doc=d;nextPage();}
    private void footer()throws IOException{
      stream.setStrokingColor(LIGHT);stream.moveTo(42,48);stream.lineTo(553,48);stream.stroke();
      textAt("ElevateControl | Gestao de manutencao",42,33,regular,8,SECONDARY);
      textAt("Pagina "+pageNumber,510,33,regular,8,SECONDARY);
    }
    private void nextPage()throws IOException{
      if(stream!=null){footer();stream.close();}
      var page=new PDPage(PDRectangle.A4);doc.addPage(page);
      stream=new PDPageContentStream(doc,page);pageNumber++;y=765;
      stream.setNonStrokingColor(BLUE);stream.addRect(0,792,595,50);stream.fill();
      textAt("ELEVATECONTROL",42,811,bold,18,Color.WHITE);
      textAt("RELATORIOS TECNICOS",420,815,regular,8,Color.WHITE);
      stream.setNonStrokingColor(Color.WHITE);
    }
    private void textAt(String t,float x,float pos,PDFont font,float size,Color color)throws IOException{
      stream.setNonStrokingColor(color);stream.beginText();stream.setFont(font,size);
      stream.newLineAtOffset(x,pos);stream.showText(safe(t));stream.endText();
    }
    private void ensure(float amount)throws IOException{if(y<72+amount)nextPage();}
    void space(float v)throws IOException{ensure(v);y-=v;}
    void line(String content,boolean strong)throws IOException{
      PDFont font=strong?bold:regular;float size=strong?11.5f:9.5f;
      if(strong){ensure(27);y-=5;stream.setStrokingColor(LIGHT);stream.moveTo(42,y+5);stream.lineTo(553,y+5);stream.stroke();}
      String normalized=safe(content);
      StringBuilder b=new StringBuilder();
      for(String word:normalized.split("\\s+")){
        String prospect=b.isEmpty()?word:b+" "+word;
        if(font.getStringWidth(prospect)/1000f*size>507&&b.length()>0){draw(b.toString(),font,size,strong);b.setLength(0);}
        if(b.length()>0)b.append(' ');b.append(word);
      }
      if(!b.isEmpty())draw(b.toString(),font,size,strong);
    }
    private void draw(String s,PDFont font,float size,boolean strong)throws IOException{
      ensure(17);textAt(s,42,y,font,size,strong?BLUE:SECONDARY);y-=strong?21:16;
    }
    void image(String label,String data)throws IOException{
      if(data==null||!data.startsWith("data:image/png;base64,"))return;
      line(label+":",false);ensure(77);
      byte[] b=Base64.getDecoder().decode(data.substring("data:image/png;base64,".length()));
      PDImageXObject image=PDImageXObject.createFromByteArray(doc,b,label);
      float width=Math.min(170,image.getWidth()*0.5f);
      float height=Math.min(55,image.getHeight()*width/Math.max(1,image.getWidth()));
      stream.drawImage(image,42,y-height,width,height);y-=height+13;
    }
    void close()throws IOException{if(stream!=null){footer();stream.close();stream=null;}}
    static String safe(String s){
      if(s==null)return "";
      String v=Normalizer.normalize(s,Normalizer.Form.NFKD).replaceAll("\\p{M}","");
      return v.replace('–','-').replace('—','-').replace('’','\'').replaceAll("[^\\x20-\\x7E]","?");
    }
 }
}
