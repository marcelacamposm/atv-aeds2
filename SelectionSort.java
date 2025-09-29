import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

public class SelectionSort<T extends Comparable<T>> implements IOrdenador<T>{
    private long comparacoes;
	private long movimentacoes;
	private LocalDateTime inicio;
	private LocalDateTime termino;	
	
	public SelectionSort() {
		comparacoes = 0;
		movimentacoes = 0;
	}
	
	@Override
	public T[] ordenar(T[] dados) {
		return ordenar(dados, T::compareTo);
	}
	
	@Override
	public T[] ordenar(T[] dados, Comparator<T> comparador) {
		T[] dadosOrdenados = Arrays.copyOf(dados, dados.length);
		int tamanho = dadosOrdenados.length;
		
		inicio = LocalDateTime.now();
		
		for (int posReferencia = 0; posReferencia < tamanho ; posReferencia++) {
            int posMenor = posReferencia;
			for (int posicao = posReferencia+1; posicao < tamanho; posicao++) {
				comparacoes++;
				if (comparador.compare(dadosOrdenados[posMenor],dadosOrdenados[posicao]) > 0){
					posMenor = posicao;
				}
			}
			swap(posReferencia, posMenor, dadosOrdenados);
		}	
		termino = LocalDateTime.now();

		return dadosOrdenados;
	}

	private void swap(int i, int j, T[] vet) {
		movimentacoes++;
		
		T temp = vet[i];
	    vet[i] = vet[j];
	    vet[j] = temp;
	}
	
	public long getComparacoes() {
		return comparacoes;
	}
	
	public long getMovimentacoes() {
		return movimentacoes;
	}
	
	public double getTempoOrdenacao() {
	    return  Duration.between(inicio, termino).toMillis();	    
	}
}
