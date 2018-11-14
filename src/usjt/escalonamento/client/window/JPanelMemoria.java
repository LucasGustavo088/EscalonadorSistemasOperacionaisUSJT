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
public class JPanelMemoria extends JPanel implements Runnable {

    private static final long serialVersionUID = 6878053371054362239L;
    protected int             tempoMili        = 0;
    protected Thread          thread           = null;
    protected int             tempoTotal       = 1;
    protected Window          window           = null;
    protected Escalonador     escalonador      = null;
    protected boolean         executando       = false;

    /**
     * Abre uma Thread para a execução do escalonador
     * 
     * @param escalonador
     *            Escalonador a ser executado
     * @param tempoMili
     *            Tempo de espera entre os "tempos"
     */
    public void start(Escalonador escalonador, int tempoMili) {
        this.thread = new Thread(this, "memoria");
        this.escalonador = escalonador;
        this.tempoMili = tempoMili;
        this.thread.start();
    }

    @Override
    public void run() {
        Color bg = null;
        Color fg = null;
        
        JLabel label = null;
        List<JLabel> labels = null;
        labels = new ArrayList<JLabel>();
        
        for (int i = 0; i < 100; i++) {
            label = new JLabel(String.format("%5d", i));
            label.setOpaque(true);
            label.setBackground(this.getBackground());
            label.setForeground(this.getBackground());
            label.setBorder(new LineBorder(Color.black, 1));
            this.add(label);
            labels.add(label);
        }

        this.executando = true;

        this.tempoTotal = 0;
        for (Processo p : this.escalonador.getProcessos()) {
            this.tempoTotal += p.getTempoEmEspera();
        }

        int[][] valoresMemoria = this.obterValoresMemoriaSJF(this.tempoTotal, this.escalonador.getProcessos());

        	this.updateUI();

            label = labels.get(0);
            label.setBackground(new Color(255, 255, 255));
            label.setForeground(fg);
            
            // rola até o label
            this.scrollRectToVisible(label.getBounds());
            this.updateUI();
    }
    
    public int[][] obterValoresMemoriaSJF(int tempoTotal, List<Processo> processos) {
    	int[][] multi = new int[tempoTotal][100];
    	
    	boolean preemptivo = this.escalonador.isPreemptivo();
;    	for(int linha = 0; linha < tempoTotal; linha++) {
    		
    	}
    	
    	return multi;
    }

    /**
     * Creates new form JPanelEscalonamento
     */
    public JPanelMemoria() {
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
        setBackground(new java.awt.Color(50, 50, 50));
        setBorder(javax.swing.BorderFactory
                        .createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new GridLayout(1, 100));
        ((GridLayout) this.getLayout()).setColumns(this.tempoTotal);
        
        
    }
}
