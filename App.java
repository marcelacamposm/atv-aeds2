import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Vetor de pedidos cadastrados */
    static Pedido[] pedidosCadastrados;
    
    /** Vetor de pedidos ordenados pela data do pedido */
    static Pedido[] pedidosOrdenadosPorData;
    
    /** Quantidade de pedidos cadastrados atualmente no vetor */
    static int quantPedidos = 0;
    
    static IOrdenator<Pedido> ordenador;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de pedidos. Arquivo-texto no formato
     * N  (quantidade de pedidos) <br/>
     * dataDoPedido;formaDePagamento;descrições dos produtos do pedido <br/>
     * Deve haver uma linha para cada um dos pedidos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os pedidos carregados, ou vazio em caso de problemas de leitura.
     */
    static Pedido[] lerPedidos(String nomeArquivoDados) {
    	
    	Pedido[] pedidosCadastrados;
    	Scanner arquivo = null;
    	int numPedidos;
    	String linha;
    	Pedido pedido;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numPedidos = Integer.parseInt(arquivo.nextLine());
    		pedidosCadastrados = new Pedido[numPedidos];
    		
    		for (int i = 0; i < numPedidos; i++) {
    			linha = arquivo.nextLine();
    			pedido = criarPedido(linha);
    			pedidosCadastrados[i] = pedido;
    		}
    		quantPedidos = numPedidos;
    		
    	} catch (IOException excecaoArquivo) {
    		pedidosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return pedidosCadastrados;
    }
    
    private static Pedido criarPedido(String dados) {
    	
    	String[] dadosPedido;
    	DateTimeFormatter formatoData;
    	LocalDate dataDoPedido;
    	int formaDePagamento;
    	Pedido pedido;
    	Produto produto;
    	
    	dadosPedido = dados.split(";");
    	
    	formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	dataDoPedido = LocalDate.parse(dadosPedido[0], formatoData);
    	
    	formaDePagamento = Integer.parseInt(dadosPedido[1]);
    	
    	pedido = new Pedido(dataDoPedido, formaDePagamento);
    	
    	for (int i = 2; i < dadosPedido.length; i++) {
    		produto = pesquisarProduto(dadosPedido[i]);
    		pedido.incluirProduto(produto);
    	}
    	return pedido;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto passado como parâmetro para esse método. 
     *  A busca não é sensível ao caso.
     *  @param pesquisado Nome do produto a ser pesquisado no vetor de produtos cadastrados. 
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto pesquisarProduto(String pesquisado) {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(pesquisado)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        if (!localizado) {
        	return null;
        } else {
        	return(produto);
        }     
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Procurar por pedidos realizados em uma data");
        System.out.println("2 - Ordenar pedidos");
        System.out.println("3 - Embaralhar pedidos");
        System.out.println("4 - Listar todos os pedidos");
        System.out.println("0 - Finalizar");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    /** Localiza pedidos no vetor de pedidos, a partir da data do pedido informada pelo usuário,
     *  e imprime seus dados.
     *  O método solicita ao usuário a data desejada (no formato dd/MM/yyyy),
     *  e, em seguida, realiza a busca  
     *  por todos os pedidos que correspondem à data informada.
     *  A busca é otimizada pela ordenação prévia do vetor de pedidos por data.
     *  Em caso de não encontrar nenhum pedido, imprime uma mensagem padrão */
    
    static void localizarPedidosPorData() {
        cabecalho();
        System.out.println("Informe a data desejada (formato dd/MM/yyyy): ");
        String dataInformada = teclado.nextLine();

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataDesejada;
        try {
            dataDesejada = LocalDate.parse(dataInformada, formatoData);
        } catch (Exception e) {
            System.out.println("Data inválida. Tente novamente.");
            return;
        }

        boolean encontrou = false;
        for (Pedido pedido : pedidosOrdenadosPorData) {
            if (pedido.getDataDoPedido().equals(dataDesejada)) {
                System.out.println(pedido);
                encontrou = true;
            } else if (pedido.getDataDoPedido().isAfter(dataDesejada)) {
                break; // Como está ordenado, não há mais pedidos com a data desejada
            }
        }

        if (!encontrou) {
            System.out.println("Nenhum pedido encontrado para a data informada.");
        }
    }
    static int exibirMenuOrdenadores() {
        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção"); 
        System.out.println("3 - Seleção"); 
        System.out.println("4 - Mergesort"); 
        System.out.println("5 - Heapsort"); 
        System.out.println("0 - Finalizar");
       
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    static int exibirMenuComparadores() {
        cabecalho();
        System.out.println("1 - Por código");
        System.out.println("2 - Por data");
        System.out.println("3 - Por valor");
        
        return lerOpcao("Digite sua opção: ", Integer.class);
    }
    
    /** Ordena o vetor de pedidos cadastrados empregando um método de ordenação selecionado pelo
     *  usuário, dentre os seguintes: bolha, seleção, inserção, mergesort e heapsort.
     *  O usuário também escolhe um critério de ordenação, a saber: por código, data ou valor do pedido. 
     *  O método interage com o usuário por meio de menus e aplica 
     *  a ordenação escolhida. 
     *  Se o critério de ordenação escolhido for a data do pedido, em caso de empate, o critério de 
     *  desempate é o código identificador do pedido.
     *  Se o critério de ordenação escolhido for o valor final do pedido, em caso de empate, o critério de 
     *  desempate é a quantidade de produtos no pedido. Em caso de novo empate, o critério de 
     *  desempate é o código identificador do pedido.
     *  Ao final, exibe o tempo total gasto no processo de ordenação, em ms. */
    
    static void ordenarPedidos() {
        int metodoOrdenacao = exibirMenuOrdenadores();
        if (metodoOrdenacao == 0) return;

        int criterioOrdenacao = exibirMenuComparadores();
        if (criterioOrdenacao < 1 || criterioOrdenacao > 3) return;

        Comparator<Pedido> comparador = null;
        switch (criterioOrdenacao) {
            case 1 -> comparador = Comparator.comparing(Pedido::getCodigo);
            case 2 -> comparador = Comparator.comparing(Pedido::getDataDoPedido)
                                             .thenComparing(Pedido::getCodigo);
            case 3 -> comparador = Comparator.comparing(Pedido::getValorFinal)
                                            .thenComparing(Pedido::getQuantidadeProdutos)
                                             .thenComparing(Pedido::getCodigo);
        }

        ordenador = switch (metodoOrdenacao) {
            case 1 -> new BubbleSort<>();
            case 2 -> new InsertionSort<>();
            case 3 -> new SelectionSort<>();
            case 4 -> new MergeSort<>();
            case 5 -> new HeapSort<>();
            default -> null;
        };

        if (ordenador == null || comparador == null) return;

        long inicio = System.currentTimeMillis();
        pedidosOrdenadosPorData = Arrays.copyOf(pedidosCadastrados, quantPedidos);
        ordenador.ordenar(pedidosOrdenadosPorData, comparador);
        long fim = System.currentTimeMillis();

        System.out.println("Ordenação concluída em " + (fim - inicio) + " ms.");
    }

    static void embaralharPedidos(){
        Collections.shuffle(Arrays.asList(pedidosCadastrados));
    }

    /** Lista todos os pedidos cadastrados, numerados, um por linha */
    static void listarTodosOsPedidos() {
    	
        cabecalho();
        System.out.println("\nPedidos cadastrados: ");
        for (int i = 0; i < quantPedidos; i++) {
        	System.out.println(String.format("%02d - %s\n", (i + 1), pedidosCadastrados[i].toString()));
        }
    }
    
    public static void main(String[] args) {
		
    	teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
    	nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
       
        String nomeArquivoPedidos = "pedidos.txt";
        pedidosCadastrados = lerPedidos(nomeArquivoPedidos);
        
        int opcao = -1;
      
        do{
        	opcao = menu();
            switch (opcao) {
                case 1 -> localizarPedidosPorData();
                case 2 -> ordenarPedidos();
                case 3 -> embaralharPedidos();
                case 4 -> listarTodosOsPedidos();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);       

        teclado.close();    
    }
}
