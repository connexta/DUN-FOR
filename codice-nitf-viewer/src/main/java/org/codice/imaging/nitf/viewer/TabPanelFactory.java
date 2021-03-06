package org.codice.imaging.nitf.viewer;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TabPanelFactory {

    @Autowired
    private ViewManager ViewManager;

    PropertiesImageTab createNitfImagePanel(final BufferedImage bufferedImage,
            final NitfHeader fileHeader, final ImageSegment header) {

        return new PropertiesImageTab(bufferedImage, fileHeader, header);
    }

    JPanel createTab(String tabName) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.add(new JLabel(tabName));

        URL iconUrl = ClassLoader.getSystemClassLoader()
                .getResource("images/Close_Tab.png");
        URL pressedIconUrl = ClassLoader.getSystemClassLoader()
                .getResource("images/Close_Tab-pressed.png");

        ImageIcon icon = new ImageIcon(iconUrl, "close this tab");
        ImageIcon pressedIcon = new ImageIcon(pressedIconUrl);
        JLabel closeLabel = new JLabel(icon);

        closeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("closing...");
                JTabbedPane jTabbedPane = ViewManager.getActiveInternalFrame().getjTabbedPane();
                jTabbedPane.remove(jTabbedPane.indexOfTabComponent(panel));
            }

            public void mousePressed(MouseEvent e) {
                closeLabel.setIcon(pressedIcon);
            }

            public void mouseReleased(MouseEvent e) {
                closeLabel.setIcon(icon);
            }
        });

        panel.add(closeLabel);

        return panel;
    }
}
