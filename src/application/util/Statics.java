package application.util;

import java.util.ArrayList;
import java.util.List;

public class Statics {

	public static class Path{
		public static String APP = "C:\\ConsultaPlacas";
		public static String APP_INPUT = APP+"\\Placas";
		public static String APP_OUTPUT = APP+"\\Resultados";
	}
	
	public static class Url{
		public static List<String> SITES = new ArrayList<String>(){
             {
                  add("https://www.detran.mg.gov.br/veiculos/veiculos-apreendidos/lista-de-veiculos-apreendidos/-/lista_veiculos_apreendidos_furtados");
                  add("http://www3.prefeitura.sp.gov.br/smt/pesqveic/Pesquisa.aspx");
                  add("http://www.detran.pe.gov.br/index.php?option=com_content&view=article&id=396&Itemid=15");
                  add("http://www.coderp.com.br/JW07/veiculoPatio.xhtml");
                  add("http://www.vtx2.com.br/Patio/");
                  add("http://www.transalvador.salvador.ba.gov.br/conteudo/index.php/veiculoApreendido/consultaPlaca");
                  //ADICIONADOS:
                  add("http://www.detran.pb.gov.br/index.php/consultar-veiculo-apreendido.html");
                  //IGNORADOS:
                  //SP: site não funciona (link obsoleto)
                  add("http://www.sptrans.com.br/veiculos_apreendidos/");
                  //RJ: site possui validação anti-robô
                  add("http://www.detran.rj.gov.br/_documento.asp?cod=8713");
                  //CE: site possui validação anti-robô
                  add("http://central.detran.ce.gov.br/veiculos/consulta_simples");
                  
             }
       };
	}
	
	public static class File{
		public static String INPUT = "placas.xlsx";
		public static String OUTPUT = "base.xlsx";
	}
}
