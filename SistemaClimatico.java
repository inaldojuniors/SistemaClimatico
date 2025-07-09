import org.json.JSONObject;
import java.net.URI; // representa um endereço da web.
import java.net.URLEncoder; // Codifica strings para uso na web.
import java.net.http.HttpClient; // Envia e recebe dados via HTTP.
import java.net.http.HttpRequest; // Representa uma solicitação HTTP.
import java.net.http.HttpResponse; // Representa uma resposta HTTP.
import java.nio.charset.StandardCharsets; //Define padrões de codificação de caracteres.
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SistemaClimatico {

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite o nome da cidade");
		String cidade = scanner.nextLine(); // Lê a cidade do teclado.

		try {
			String dadosClimaticos = getDadosClimaticos(cidade); //Retora um json

			//Codigo 1006 significa localização não encontrada.
			if (dadosClimaticos.contains("\"code\":1006")){//\"code\": 1006 representa "code":1006
				System.out.println("Localização não encontrada. Por favor, tente novamente.");
			} else {
				imprimirDadosClimaticos(dadosClimaticos);
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public static String getDadosClimaticos(String cidade) throws Exception {
		String apiKey = Files.readString(Paths.get("api-key.txt")).trim();

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" +apiKey + "&q=" + formataNomeCidade;
		HttpRequest request = HttpRequest.newBuilder() // Começa a construção de uma nova solicitação httpo.
			.uri(URI.create(apiUrl)) // Define o URI da solicitação http.
			.build(); //Finaliza a construção da solicitação HTTP.

		// Criar objeto enviar solicitação HTTP e receber resposta HTTp, para acessar o site da WetherApi
		HttpClient client = HttpClient.newHttpClient();

		// Agora vamos enviar rrequisições http e receber respostas http. Comunicar com o site da Api meteorologic.
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body(); // Retorna os dados meteorologicos obtidos no site da API
	}

	// Método para imprimir os dados meteorologicos de forma organizada
	public static void imprimirDadosClimaticos (String dados){
		//System.out.println("Dados originais(JSON) obtidos no site meteorologico" + dados);
		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

		// Extrai dados da localização
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		//Extrai dados adicionais
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");
		int umidade = informacoesMeteorologicas.getInt("humidity");
		float velocidadeDoVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		//extrai a data da string retornada pela API.
		String dataHoraString = informacoesMeteorologicas.getString("last_updated");

		//Imprimir informações atuais.
		System.out.println("Informações Meteorologicas para " + cidade + "," + pais);
		System.out.println("Data e Hora:  " + dataHoraString);
		System.out.println("Temperatura Atual: " + temperaturaAtual + "C");
		System.out.println("Sensação Térmica: " + sensacaoTermica + "C");
		System.out.println("Condição do Tempo: " + condicaoTempo);
		System.out.println("Umidade " + umidade + "%");
		System.out.println("Velocidade do Vento: " + velocidadeDoVento + "km/h");
		System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + "mb");

	}

}