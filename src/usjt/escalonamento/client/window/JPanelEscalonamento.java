package usjt.escalonamento.client.window;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import usjt.escalonamento.Escalonador;
import usjt.escalonamento.Processo;

/**
 * Grafico de Escalonamento
 */
public class JPanelEscalonamento extends JPanel implements Runnable {

    private static final long serialVersionUID = 6878053371054362239L;
    protected int             tempoMili        = 0;
    protected Thread          thread           = null;
    protected int             tempoTotal       = 1;
    protected Window          window           = null;
    protected Escalonador     escalonador      = null;
    protected boolean         executando       = false;
    protected char[][] 		  memoria; 		  

    /**
     * Abre uma Thread para a execução do escalonador
     * 
     * @param escalonador
     *            Escalonador a ser executado
     * @param tempoMili
     *            Tempo de espera entre os "tempos"
     */
    public void start(Escalonador escalonador, int tempoMili) {
        this.thread = new Thread(this, "thread_escalonador");
        this.escalonador = escalonador;
        this.tempoMili = tempoMili;
        this.thread.start();
    }

    @Override
    public void run() {
        Color bg = null;
        Color fg = null;
        List<JLabel> labels = null;
        JLabel label = null;
        

        this.executando = true;

        this.tempoTotal = 0;
        for (Processo p : this.escalonador.getProcessos()) {
            this.tempoTotal += p.getTempoExecucao();
        }
        
        memoria = new char[this.tempoTotal][100];

        ((GridLayout) this.getLayout()).setColumns(this.tempoTotal);

        this.removeAll();

        labels = new ArrayList<JLabel>();

        for (int i = 0; i < this.tempoTotal; i++) {
            label = new JLabel(String.format("%5d", i));
            label.setOpaque(true);
            label.setBackground(this.getBackground());
            label.setForeground(this.getBackground());
            label.setBorder(new LineBorder(Color.black, 1));
            this.add(label);
            labels.add(label);
        }

        this.updateUI();

        try {
            while (!this.escalonador.isTerminado() && this.executando) {
                if (this.tempoMili > 0)
                    Thread.sleep(this.tempoMili);

                escalonador.passaTempo();
                
                if (escalonador.getCorrente() != null) {
                	if(escalonador.getTempoAtual() != 0 && escalonador.getTempoAtual() != this.tempoTotal) {
                		for(int i = 0; i < 100; i++) {
                			this.memoria[escalonador.getTempoAtual()][i] = this.memoria[escalonador.getTempoAtual() - 1][i];
                		}
                		
                	}
                	
                	//Retirando processos não prontos
                	for(int i = 0; i < 100; i++) {
                		
                		char c = this.memoria[escalonador.getTempoAtual()][i];
                		boolean naoAchou = true;
                		for(Processo pronto : this.escalonador.getProntos()) {
                			if(c == pronto.getNome().charAt(0)) {
                				naoAchou = false;
                			}
                		}
                		
                		if(naoAchou) {
                			this.memoria[escalonador.getTempoAtual()][i] = 0;
                		}
            		}
                	
                	for(Processo pronto : this.escalonador.getProntos()) {
                		//Verificando se o processo ja está no array
                		boolean achouProcessoNaLinha = false;
                		for(int i = 0; i < 100; i++) {
                    		if(pronto.getNome().charAt(0) == this.memoria[escalonador.getTempoAtual()][i]) {
                    			achouProcessoNaLinha = true;
                    		}
                		}
                		
                		if(achouProcessoNaLinha) {
                			continue;
                		}
                		
                		int unidadeMemoriaAtual = 0;
                    	int unidadeMemoriaAtualAte = 0;
                    	int espacoNecessarioAchado = 0;
                    	boolean achouEspaco = false;
                    	
                    	for(unidadeMemoriaAtual = 0; unidadeMemoriaAtual < 100; unidadeMemoriaAtual++) {
                    		
                    		if(this.memoria[this.escalonador.getTempoAtual()][unidadeMemoriaAtual] != 0) {
                    			continue;
                    		}
                    		
                    		for(unidadeMemoriaAtualAte = unidadeMemoriaAtual; unidadeMemoriaAtualAte < unidadeMemoriaAtual + pronto.getTempoExecucaoEstatico() && unidadeMemoriaAtualAte < 100; unidadeMemoriaAtualAte++) {
                    			if(this.memoria[this.escalonador.getTempoAtual()][unidadeMemoriaAtualAte] == 0) {
                    				espacoNecessarioAchado++;
                    			}
                    			
                    			if(espacoNecessarioAchado == pronto.getTempoExecucaoEstatico()) {
                    				achouEspaco = true;
                    				break;
                    			}
                    		}
                    		
                    		if(achouEspaco == true) {
                    			break;
                    		}
                    		
                    	}                	
                    	
                    	if(achouEspaco) {
                    		for(int i = unidadeMemoriaAtual; i <= unidadeMemoriaAtualAte; i++) {
                    			this.memoria[this.escalonador.getTempoAtual()][i] = pronto.getNome().charAt(0);
                    		}
                    	}
                	}
                	
                	
                    bg = this.window.getColorOfProcesso(this.escalonador
                                    .getCorrente());
                    fg = new Color(255 - bg.getRed(), 255 - bg.getGreen(), 255 - bg
                                    .getBlue());

                    label = labels.get(this.escalonador.getTempoAtual());
                    label.setBackground(bg);
                    label.setForeground(fg);
                    
                    // rola até o label
                    this.scrollRectToVisible(label.getBounds());
                    this.updateUI();
                }
            }
            this.window.memoriaFuncionando = this.memoria;
            
            this.window.fimEscalonamento();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.executando = false;
        }
    }

    /**
     * Creates new form JPanelEscalonamento
     */
    public JPanelEscalonamento() {
        initComponents();
    }

    /**
     * Para a execução do JPanelEscalonamento
     */
    public void stop() {
        this.executando = false;
    }

    public Window getWindow() {
        return this.window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public Escalonador getEscalonador() {
        return this.escalonador;
    }

    public void setEscalonador(Escalonador escalonador) {
        this.escalonador = escalonador;
    }

    public boolean isExecutando() {
        return this.executando;
    }

    private void initComponents() {
        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory
                        .createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new GridLayout(1, 100));
    }
    
    public void printMemoria()
    {
       for(int i = 0; i < this.escalonador.getTempoAtual(); i++)
       {
          for(int j = 0; j < 100; j++)
          {
             System.out.print(this.memoria[i][j]);
          }
          System.out.println();
       }
    }
}
