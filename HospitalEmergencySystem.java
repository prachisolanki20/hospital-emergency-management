import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class HospitalEmergencySystem extends JFrame {
    static class Patient {
        String id, name; int age, priority, arrival;
        Patient(String id,String name,int age,int priority,int arrival){
            this.id=id;this.name=name;this.age=age;this.priority=priority;this.arrival=arrival;
        }
        public String toString(){return id+" | "+name+" | Age "+age+" | "+priorityName(priority);}
    }
    ArrayList<Patient> heap=new ArrayList<>();
    int arrival=0, processed=0;
    JTextField id=new JTextField(), name=new JTextField(), age=new JTextField();
    JComboBox<String> priority=new JComboBox<>(new String[]{"4 - Critical","3 - Urgent","2 - Moderate","1 - Stable"});
    JTextArea heapText=new JTextArea(), logText=new JTextArea(), message=new JTextArea(2,20);
    HeapTreePanel treePanel = new HeapTreePanel();
    JLabel waiting=new JLabel("0"), next=new JLabel("—"), done=new JLabel("0");
    Color navy=new Color(23,43,77), blue=new Color(47,98,180), pale=new Color(234,241,255);

    static String priorityName(int p){return switch(p){case 4->"Critical";case 3->"Urgent";case 2->"Moderate";default->"Stable";};}
    boolean higher(Patient a,Patient b){return a.priority!=b.priority?a.priority>b.priority:a.arrival<b.arrival;}
    void log(String s){logText.insert("• "+s+"\n",0);}
    void showMessage(String s){message.setText(s);}
    void swap(int i,int j){Collections.swap(heap,i,j);log("Swapped elements to restore Heap property.");}
    void up(int i){while(i>0){int p=(i-1)/2;if(!higher(heap.get(i),heap.get(p)))break;swap(i,p);i=p;}}
    void down(int i){while(true){int l=2*i+1,r=2*i+2,b=i;
        if(l<heap.size()&&higher(heap.get(l),heap.get(b)))b=l;
        if(r<heap.size()&&higher(heap.get(r),heap.get(b)))b=r;
        if(b==i)break;swap(i,b);i=b;}}
    void addPatient(){
        String pid=id.getText().trim(), pname=name.getText().trim(); int a,p;
        try{a=Integer.parseInt(age.getText().trim());p=4-priority.getSelectedIndex();}
        catch(Exception e){JOptionPane.showMessageDialog(this,"Enter a valid age.");return;}
        if(pid.isEmpty()||pname.isEmpty()||a<0||a>120){JOptionPane.showMessageDialog(this,"Enter ID, name and age from 0 to 120.");return;}
        for(Patient x:heap)if(x.id.equalsIgnoreCase(pid)){JOptionPane.showMessageDialog(this,"Patient ID already waiting.");return;}
        heap.add(new Patient(pid,pname,a,p,++arrival));log("Added "+pname+" with priority "+p+". Heapify-Up started.");up(heap.size()-1);
        showMessage("Added "+pname+". Heapify-Up moves a higher-priority patient towards the root.");
        id.setText("");name.setText("");age.setText("");refresh();
    }
    void processPatient(){
        if(heap.isEmpty()){showMessage("No patients waiting.");return;}
        Patient p=heap.get(0), last=heap.remove(heap.size()-1);
        if(!heap.isEmpty()){heap.set(0,last);log("Removed root; moved last patient to root. Heapify-Down started.");down(0);}
        processed++;log("Processed "+p.name+" ("+p.id+").");
        showMessage("Processed: "+p+"\nHeapify-Down restored the Max-Heap.");refresh();
    }
    void refresh(){
        waiting.setText(""+heap.size());done.setText(""+processed);
        next.setText(heap.isEmpty()?"—":heap.get(0).priority+" — "+priorityName(heap.get(0).priority));
        StringBuilder s=new StringBuilder("LIVE MAX-HEAP (array order)\n\n");
        if(heap.isEmpty())s.append("No patients waiting.\n");
        for(int i=0;i<heap.size();i++){Patient p=heap.get(i);s.append(i==0?"★ ROOT  ":"        ").append(p).append(" | Arrival #").append(p.arrival).append("\n");}
        s.append("\nARRAY: [");for(int i=0;i<heap.size();i++){if(i>0)s.append(", ");s.append(heap.get(i).id).append(":").append(heap.get(i).priority);}s.append("]\n\nWAITING LIST (priority order)\n");
        ArrayList<Patient> sorted=new ArrayList<>(heap);sorted.sort((a,b)->higher(a,b)?-1:higher(b,a)?1:0);
        for(Patient p:sorted)s.append(p).append(" | Arrival #").append(p.arrival).append("\n");
        s.append("\nParent=(i-1)/2, Left=2i+1, Right=2i+2");
        heapText.setText(s.toString());heapText.setCaretPosition(0);treePanel.repaint();treePanel.revalidate();
    }
    void search(){
        String key=JOptionPane.showInputDialog(this,"Enter Patient ID:");if(key==null||key.trim().isEmpty())return;
        for(Patient p:heap)if(p.id.equalsIgnoreCase(key.trim())){showMessage("Found: "+p+" | Arrival #"+p.arrival);log("Search found "+p.id+".");return;}
        showMessage("Patient "+key+" not found in waiting Heap.");log("Search did not find "+key+".");
    }
    void samples(){
        Patient[] data={new Patient("P101","Aarav Shah",42,2,0),new Patient("P102","Mira Patel",28,4,0),new Patient("P103","Kabir Rao",65,3,0),new Patient("P104","Diya Mehta",19,2,0)};
        int n=0;for(Patient p:data){boolean exists=false;for(Patient x:heap)if(x.id.equalsIgnoreCase(p.id))exists=true;
            if(!exists){p.arrival=++arrival;heap.add(p);up(heap.size()-1);log("Loaded sample "+p.name+".");n++;}}
        showMessage(n>0?"Sample patients loaded. Observe the highest priority at the root.":"Sample patients already loaded.");refresh();
    }
    JPanel card(String title){JPanel p=new JPanel(new BorderLayout(8,8));p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,239)),new EmptyBorder(14,14,14,14)));
        JLabel l=new JLabel(title);l.setFont(new Font("SansSerif",Font.BOLD,16));l.setForeground(navy);p.add(l,BorderLayout.NORTH);return p;}
    JButton btn(String text,Color bg,Color fg){JButton b=new JButton(text);b.setBackground(bg);b.setForeground(fg);b.setFocusPainted(false);b.setFont(new Font("SansSerif",Font.BOLD,12));return b;}
    JPanel stat(String title,JLabel value){JPanel p=new JPanel(new GridLayout(2,1));p.setBackground(Color.WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,231,239)),new EmptyBorder(9,12,9,12)));
        JLabel t=new JLabel(title);t.setForeground(Color.GRAY);value.setFont(new Font("SansSerif",Font.BOLD,21));value.setForeground(navy);p.add(t);p.add(value);return p;}
    class HeapTreePanel extends JPanel {
        private final int nodeW = 166, nodeH = 62, levelGap = 105, sideGap = 22;

        HeapTreePanel() {
            setOpaque(true);
            setBackground(new Color(249,251,254));
            setPreferredSize(new Dimension(700, 250));
        }

        private int levelOf(int index) {
            return 31 - Integer.numberOfLeadingZeros(index + 1);
        }

        private int maxLevel() {
            int max = 0;
            for (int i = 0; i < heap.size(); i++) max = Math.max(max, levelOf(i));
            return max;
        }

        private Point centerOf(int index, int width) {
            int level = levelOf(index);
            int first = (1 << level) - 1;
            int position = index - first;
            int nodesOnLevel = 1 << level;
            int usable = Math.max(width, nodesOnLevel * (nodeW + sideGap));
            int x = (int) ((position + 0.5) * usable / nodesOnLevel);
            int y = 34 + level * levelGap;
            return new Point(x, y);
        }

        @Override public Dimension getPreferredSize() {
            int deepest = maxLevel();
            int widestLevel = 1 << deepest;
            int w = Math.max(700, widestLevel * (nodeW + sideGap));
            int h = Math.max(230, 70 + (deepest + 1) * levelGap);
            return new Dimension(w, h);
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (heap.isEmpty()) {
                g.setColor(new Color(100,116,139));
                g.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g.drawString("No patients waiting. Add a patient or load samples.", 24, 42);
                g.dispose();
                return;
            }

            int width = Math.max(getWidth(), getPreferredSize().width);

            // Draw parent-child links before drawing the nodes.
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(148,163,184));
            for (int i = 1; i < heap.size(); i++) {
                int parent = (i - 1) / 2;
                Point a = centerOf(parent, width);
                Point b = centerOf(i, width);
                g.drawLine(a.x, a.y + nodeH / 2 - 3, b.x, b.y - nodeH / 2 + 3);
            }

            for (int i = 0; i < heap.size(); i++) {
                Patient p = heap.get(i);
                Point c = centerOf(i, width);
                int x = c.x - nodeW / 2, y = c.y - nodeH / 2;
                Color fill = p.priority == 4 ? new Color(254,226,226)
                    : p.priority == 3 ? new Color(254,243,199)
                    : p.priority == 2 ? new Color(219,234,254)
                    : new Color(220,252,231);
                Color border = p.priority == 4 ? new Color(220,38,38)
                    : p.priority == 3 ? new Color(217,119,6)
                    : p.priority == 2 ? new Color(37,99,235)
                    : new Color(22,163,74);

                g.setColor(fill);
                g.fillRoundRect(x, y, nodeW, nodeH, 16, 16);
                g.setColor(border);
                g.setStroke(new BasicStroke(i == 0 ? 2.5f : 1.4f));
                g.drawRoundRect(x, y, nodeW, nodeH, 16, 16);

                g.setColor(new Color(15,23,42));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                String firstLine = (i == 0 ? "★ ROOT  " : "") + p.id + " | " + p.name;
                FontMetrics fm = g.getFontMetrics();
                while (firstLine.length() > 8 && fm.stringWidth(firstLine) > nodeW - 12) {
                    firstLine = firstLine.substring(0, firstLine.length() - 2) + "…";
                }
                g.drawString(firstLine, x + 8, y + 24);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g.drawString("Priority " + p.priority + " - " + priorityName(p.priority)
                    + "  |  Age " + p.age, x + 8, y + 44);
            }
            g.dispose();
        }
    }

    HospitalEmergencySystem(){
        super("Hospital Emergency Management System");setDefaultCloseOperation(EXIT_ON_CLOSE);getContentPane().setBackground(new Color(244,247,251));setLayout(new BorderLayout(10,10));
        JPanel header=new JPanel(new BorderLayout());header.setBackground(navy);header.setBorder(new EmptyBorder(17,20,17,20));
        JLabel title=new JLabel("<html><b style='font-size:18px;color:white'>✚ Hospital Emergency Management</b><br><span style='color:#cbd8ec'>Priority Queue · Max-Heap Demonstration</span></html>");
        header.add(title,BorderLayout.CENTER);JLabel tag=new JLabel("DSA PROJECT");tag.setForeground(Color.WHITE);header.add(tag,BorderLayout.EAST);add(header,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(10,10));body.setOpaque(false);body.setBorder(new EmptyBorder(12,16,14,16));
        JPanel stats=new JPanel(new GridLayout(1,3,10,0));stats.setOpaque(false);stats.add(stat("WAITING PATIENTS",waiting));stats.add(stat("NEXT PRIORITY",next));stats.add(stat("PROCESSED",done));body.add(stats,BorderLayout.NORTH);
        JPanel columns=new JPanel(new GridLayout(1,2,10,0));columns.setOpaque(false);
        JPanel form=card("Register patient");JPanel fields=new JPanel(new GridLayout(0,2,7,8));fields.setOpaque(false);
        fields.add(new JLabel("Patient ID"));fields.add(id);fields.add(new JLabel("Name"));fields.add(name);fields.add(new JLabel("Age"));fields.add(age);fields.add(new JLabel("Priority"));fields.add(priority);
        JButton add=btn("+ Add patient",blue,Color.WHITE), view=btn("View next",pale,navy), proc=btn("Process patient",new Color(255,235,237),new Color(166,35,49)), find=btn("Search ID",new Color(240,243,248),navy), sample=btn("Load samples",new Color(240,243,248),navy), reset=btn("Reset",new Color(240,243,248),navy);
        JPanel buttons=new JPanel(new GridLayout(0,2,7,7));buttons.setOpaque(false);buttons.add(add);buttons.add(view);buttons.add(proc);buttons.add(find);buttons.add(sample);buttons.add(reset);
        JPanel left=new JPanel(new BorderLayout(9,12));left.setOpaque(false);left.add(fields,BorderLayout.NORTH);left.add(buttons,BorderLayout.CENTER);
        message.setEditable(false);message.setLineWrap(true);message.setWrapStyleWord(true);message.setBackground(new Color(247,249,252));message.setBorder(new EmptyBorder(8,8,8,8));left.add(message,BorderLayout.SOUTH);form.add(left,BorderLayout.CENTER);
        JPanel heapCard=card("Live Max-Heap");
        JLabel note=new JLabel("Root = highest priority · Parent and child links shown below");
        note.setForeground(Color.GRAY);
        treePanel.setBackground(new Color(249,251,254));
        JScrollPane treeScroll=new JScrollPane(treePanel);
        treeScroll.setBorder(BorderFactory.createLineBorder(new Color(205,215,228)));
        JPanel visualArea = new JPanel(new BorderLayout(0,6));
        visualArea.setOpaque(false);
        visualArea.add(note,BorderLayout.NORTH);
        visualArea.add(treeScroll,BorderLayout.CENTER);
        heapCard.add(visualArea,BorderLayout.CENTER);
        heapText.setEditable(false);heapText.setFont(new Font(Font.MONOSPACED,Font.PLAIN,11));
        heapText.setBackground(new Color(249,251,254));heapText.setRows(5);
        JScrollPane detailsScroll=new JScrollPane(heapText);
        detailsScroll.setPreferredSize(new Dimension(300,135));
        heapCard.add(detailsScroll,BorderLayout.SOUTH);
        columns.add(form);columns.add(heapCard);body.add(columns,BorderLayout.CENTER);
        JPanel logCard=card("Operation log");logText.setEditable(false);logText.setRows(4);logCard.add(new JScrollPane(logText),BorderLayout.CENTER);body.add(logCard,BorderLayout.SOUTH);add(body,BorderLayout.CENTER);
        add.addActionListener(e->addPatient());view.addActionListener(e->{if(heap.isEmpty())showMessage("No patients waiting.");else{Patient p=heap.get(0);showMessage("Next patient: "+p+" | Arrival #"+p.arrival);log("Viewed root: "+p.name+" is next.");}});
        proc.addActionListener(e->processPatient());find.addActionListener(e->search());sample.addActionListener(e->samples());
        reset.addActionListener(e->{if(JOptionPane.showConfirmDialog(this,"Clear waiting patients and reset counts?","Reset",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){heap.clear();arrival=0;processed=0;logText.setText("");showMessage("Demo reset.");log("System reset.");refresh();}});
        showMessage("Welcome! Add a patient or load sample patients.");log("System ready.");refresh();setSize(1040,760);setMinimumSize(new Dimension(850,650));setLocationRelativeTo(null);setVisible(true);
    }
    public static void main(String[] args){SwingUtilities.invokeLater(HospitalEmergencySystem::new);}
}
