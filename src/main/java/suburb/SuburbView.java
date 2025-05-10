package suburb;

import drone.Location;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * SuburbView – GUI-based visualisation of the suburb using JTable.
 * Implements the Observer pattern to respond to drone delivery events.
 * Each road cell is updated dynamically based on drone actions.
 */
class SuburbView implements Location.Observer {

    // ========== Observer Callback ==========
    @Override
    public void notifyEvent(Location.Id id, String s, Location.DroneEvent e) {
        int i, j;
        // Identify grid coordinates based on the type of location ID
        if (id.getClass() == Suburb.StreetId.class) {
            Suburb.StreetId sid = (Suburb.StreetId) id;
            i = sid.street * 3 - 2; // Middle row for road
            j = (sid.oddHouse - 1) / 2 + 2; // Column index for house
        } else if (id.getClass() == Suburb.AvenueId.class) {
            Suburb.AvenueId aid = (Suburb.AvenueId) id;
            i = aid.numSouth - 1;
            j = (aid.avenue == Suburb.Avenue.Out ? 1 : NUMHOUSES + 2); // Left or right avenue
        } else {
            throw new IllegalArgumentException("Unknown id type: " + id.getClass());
        }

        // Update cell based on drone event type
        switch (e) {
            case arrive, endDelivery -> tm.setValueAt("[" + s + "]", i, j);
            case startDelivery -> tm.setValueAt("v" + s + "v", i, j);
            case depart -> tm.setValueAt("", i, j);
        }
    }

    final JFrame f;
    final TableModel tm;
    final int NUMSTREETS, NUMHOUSES;

    /**
     * Constructs the SuburbView with the specified number of streets and houses.
     * Dynamically builds a grid using JTable to reflect the suburb layout.
     */
    SuburbView(int NUMSTREETS, int NUMHOUSES) {
        this.NUMSTREETS = NUMSTREETS;
        this.NUMHOUSES = NUMHOUSES;

        Location.addObserver(this); // Register as a drone movement observer

        Object[][] objects = new Object[NUMSTREETS * 3][NUMHOUSES + 3];
        String[] headings = new String[NUMHOUSES + 3];

        // ========== Headings ==========
        headings[0] = "Street";
        headings[1] = "Out Ave";
        headings[NUMHOUSES + 2] = "Back Ave";
        for (int i = 1; i <= NUMHOUSES; i++) {
            headings[i + 1] = "H" + i;
        }

        // ========== Avenue Placeholders ==========
        for (int i = 0; i < NUMSTREETS * 3; i++) {
            objects[i][1] = ""; // Out Ave
            objects[i][NUMHOUSES + 2] = ""; // Back Ave
        }

        // ========== Streets and House Numbers ==========
        for (int i = 0; i < NUMSTREETS; i++) {
            objects[i * 3 + 1][0] = Suburb.StreetName.values[i] + " Street";
            for (int j = 0; j < NUMHOUSES; j++) {
                objects[i * 3][j + 2] = Integer.toString(j * 2 + 1);      // Odd houses (top row)
                objects[i * 3 + 1][j + 2] = "";                          // Street road (middle)
                objects[i * 3 + 2][j + 2] = Integer.toString((j + 1) * 2); // Even houses (bottom row)
            }
        }

        // ========== Table Setup ==========
        tm = new DefaultTableModel(objects, headings);
        JTable jt = new JTable(tm);
        jt.setBounds(100, 100, 800, 600);
        jt.setRowHeight(50);

        // ========== Renderers ==========
        DefaultTableCellRenderer roadRenderer = new DefaultTableCellRenderer();
        roadRenderer.setHorizontalAlignment(JLabel.CENTER);
        roadRenderer.setBackground(Color.GRAY);
        roadRenderer.setForeground(Color.YELLOW);

        DefaultTableCellRenderer streetNameRenderer = new DefaultTableCellRenderer();
        streetNameRenderer.setHorizontalAlignment(JLabel.CENTER);
        streetNameRenderer.setBackground(Color.LIGHT_GRAY);

        // Custom renderer for road/houses grid
        class CustomRenderer extends DefaultTableCellRenderer {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 3 == 1) {
                    cell.setBackground(Color.GRAY);
                    cell.setForeground(Color.YELLOW);
                } else {
                    cell.setBackground(Color.BLACK);
                    cell.setForeground(Color.WHITE);
                }
                return cell;
            }
        }

        // Apply renderers
        jt.getColumnModel().getColumn(0).setCellRenderer(streetNameRenderer);
        jt.getColumnModel().getColumn(0).setPreferredWidth(50);
        jt.getColumnModel().getColumn(1).setCellRenderer(roadRenderer);
        jt.getColumnModel().getColumn(1).setPreferredWidth(15);
        jt.getColumnModel().getColumn(NUMHOUSES + 2).setCellRenderer(roadRenderer);
        jt.getColumnModel().getColumn(NUMHOUSES + 2).setPreferredWidth(15);

        CustomRenderer customRenderer = new CustomRenderer();
        customRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < NUMHOUSES + 2; i++) {
            jt.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
            jt.getColumnModel().getColumn(i).setPreferredWidth(15);
        }

        // ========== Window Display ==========
        JScrollPane sp = new JScrollPane(jt);
        f = new JFrame();
        f.setTitle("Suburb");
        f.add(sp);
        f.setSize(600, 600);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
