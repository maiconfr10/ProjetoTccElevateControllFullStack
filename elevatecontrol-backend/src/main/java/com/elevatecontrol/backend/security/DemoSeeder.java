package com.elevatecontrol.backend.security;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration @Profile("demo")
public class DemoSeeder {
 @Bean ApplicationRunner seedDemo(ClienteRepository clients,PredioRepository buildings,ElevadorRepository elevators,
 UsuarioRepository users,ChamadoRepository tickets,ChecklistRepository checklists,PasswordEncoder encoder){return args->{
   if(users.count()>0||clients.count()>0)return;
   String[][] c={{"CLI-01","Condominio Aurora","00.000.000/0001-01","Mariana Lopes","aurora@exemplo.com","(21) 90000-0001"},
                 {"CLI-02","Grupo Horizonte","00.000.000/0001-02","Pedro Silva","horizonte@exemplo.com","(21) 90000-0002"},
                 {"CLI-03","Edificio Atlantico","00.000.000/0001-03","Daniela Costa","atlantico@exemplo.com","(21) 90000-0003"}};
   for(var a:c){Cliente v=new Cliente();v.setId(a[0]);v.setNome(a[1]);v.setDocumento(a[2]);v.setContato(a[3]);v.setEmail(a[4]);v.setTelefone(a[5]);clients.save(v);}
   String[][] p={{"PRD-01","CLI-01","Residencial Aurora","Rio de Janeiro - RJ"},{"PRD-02","CLI-02","Torre Horizonte","Rio de Janeiro - RJ"},
      {"PRD-03","CLI-03","Edificio Atlantico","Rio de Janeiro - RJ"},{"PRD-04","CLI-01","Bloco Aurora II","Rio de Janeiro - RJ"}};
   for(var a:p){Predio v=new Predio();v.setId(a[0]);v.setClienteId(a[1]);v.setNome(a[2]);v.setEndereco(a[3]);buildings.save(v);}
   String[][] e={{"ELV-01","PRD-01","Social 01","Atlas","LX-400","Operacional"},
      {"ELV-02","PRD-01","Servico 02","Schindler","S-200","Em manutenção"},
      {"ELV-03","PRD-02","Principal 01","Otis","Gen2","Operacional"},
      {"ELV-04","PRD-03","Torre A","Thyssenkrupp","Evolution","Fora de serviço"},
      {"ELV-05","PRD-04","Social 01","Atlas","LX-400","Operacional"}};
   for(var a:e){Elevador v=new Elevador();v.setId(a[0]);v.setPredioId(a[1]);v.setIdentificacao(a[2]);v.setMarca(a[3]);v.setModelo(a[4]);v.setStatus(a[5]);v.setPendencia("");elevators.save(v);}
   String[][] u={{"USR-01","Camila Ferreira","camila@exemplo.com","admin",null},
      {"USR-02","Rafael Oliveira","rafael@exemplo.com","tecnico",null},
      {"USR-03","Bruno Mendes","bruno@exemplo.com","tecnico",null},
      {"USR-04","Mariana Lopes","mariana@exemplo.com","cliente","CLI-01"},
      {"USR-05","Pedro Silva","pedro@exemplo.com","cliente","CLI-02"}};
   for(var a:u){Usuario v=new Usuario();v.setId(a[0]);v.setNome(a[1]);v.setEmail(a[2]);v.setPerfil(a[3]);v.setClienteId(a[4]);v.setRegistro("tecnico".equals(a[3])?"TEC-"+a[0].substring(4):null);
      v.setSenhaHash(encoder.encode("Demo@123"));v.setAtivo(true);v.setTrocaSenhaObrigatoria(false);users.save(v);}
   String[][] ticketsSeed={{"CH-1001","Ruido durante a subida","PRD-01","ELV-02","Corretiva","Alta","Em andamento","USR-02","USR-04"},
     {"CH-1002","Manutencao preventiva mensal","PRD-02","ELV-03","Preventiva","Normal","Aberto","USR-03","USR-01"},
     {"CH-1003","Elevador parou entre andares","PRD-03","ELV-04","Emergência","Crítica","Aberto",null,"USR-01"}};
   for(var a:ticketsSeed){Chamado v=new Chamado();v.setId(a[0]);v.setTitulo(a[1]);v.setDescricao("Chamado demonstrativo para testes do TCC.");v.setPredioId(a[2]);v.setElevadorId(a[3]);v.setTipo(a[4]);v.setPrioridade(a[5]);v.setStatus(a[6]);v.setTecnicoId(a[7]);v.setCriadoPor(a[8]);v.setData(LocalDateTime.now().minusDays(2));tickets.save(v);}
 };}
}
