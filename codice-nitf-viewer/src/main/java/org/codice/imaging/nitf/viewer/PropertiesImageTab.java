package org.codice.imaging.nitf.viewer;

import org.codice.imaging.nitf.core.common.TaggedRecordExtensionHandler;
import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageCoordinatePair;
import org.codice.imaging.nitf.core.image.ImageCoordinates;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;
import org.codice.imaging.nitf.core.tre.Tre;
import org.codice.imaging.nitf.core.tre.TreEntry;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Supplier;

public class PropertiesImageTab extends JSplitPane {

    private ImagePanel imagePanel;

    private ImageSegment imageSegment;

    private static final String[] HEADERS = {"Property", "Value"};

    public PropertiesImageTab(final BufferedImage bufferedImage, final NitfHeader fileHeader,
            final ImageSegment imageSegment) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        this.imageSegment = imageSegment;
        this.imagePanel = new ImagePanel(bufferedImage, 400);
        JTable imageProperties = getImagePropertyTable();
        JScrollPane imagePropertiesScrollPane = new JScrollPane(imageProperties);
        JTabbedPane imagePropertiesTab = new JTabbedPane();
        imagePropertiesTab.addTab("Image Properties", imagePropertiesScrollPane);
        addTreTabs(imageSegment, imagePropertiesTab);

        JTable fileProperties = getFilePropertyTable(fileHeader);

        JScrollPane filePropertiesScrollPane = new JScrollPane(fileProperties);
        JTabbedPane filePropertiesTab = new JTabbedPane();
        filePropertiesTab.addTab("File Properties", filePropertiesScrollPane);
        addTreTabs(fileHeader, filePropertiesTab);
        
        JSplitPane propertiesPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        propertiesPane.setDividerLocation(260);
        propertiesPane.setTopComponent(imagePropertiesTab);
        propertiesPane.setBottomComponent(filePropertiesTab);

        this.setLeftComponent(imagePanel);
        this.setRightComponent(propertiesPane);

        SwingUtilities.invokeLater(() -> propertiesPane.setDividerLocation(0.7d));
    }

    private void addTreTabs(TaggedRecordExtensionHandler taggedRecordExtensionHandler, JTabbedPane imagePropertiesTab) {
        taggedRecordExtensionHandler.getTREsRawStructure().getTREs().stream()
                .forEach(tre -> {
                    String[][] treData = convertTreToStringArray2D(tre);
                    JTable tabProperties = new JTable(treData, HEADERS);
                    JScrollPane treScrollPane = new JScrollPane(tabProperties);
                    imagePropertiesTab.addTab(tre.getName(), treScrollPane);
                });
    }

    private static String[][] convertTreToStringArray2D(Tre tre) {
        List<TreEntry> treEntryList = tre.getEntries();
        String[][] treData = new String[treEntryList.size()][2];

        for (int i = 0; i < treEntryList.size(); i++) {
            TreEntry treEntry = treEntryList.get(i);
            treData[i][0] = treEntry.getName();
            treData[i][1] = treEntry.getFieldValue();
        }

        return treData;
    }

    private static JTable getFilePropertyTable(NitfHeader header) {
        FileSecurityMetadata securityMetadata = header.getFileSecurityMetadata();

        String[][] data = {{"Title:", header.getFileTitle()}, {"ID:", header.getFileTitle()},
                {"Classification:", securityMetadata.getSecurityClassification().name()},
                {"Origin ID:", header.getOriginatingStationId()},
                {"Origin Phone:", header.getOriginatorsPhoneNumber()},
                {"Standard Type:", header.getStandardType()},
                {"Complexity Level:", "" + header.getComplexityLevel()},
                {"Date:", header.getFileDateTime().getSourceString()},
                {"Class Authority:", securityMetadata.getClassificationAuthority()},
                {"Class Auth Type:", securityMetadata.getClassificationAuthorityType()},
                {"Class Text:", securityMetadata.getClassificationText()},
                {"Class Reason:", securityMetadata.getClassificationReason()}};

        JTable fileProperties = new JTable(data, HEADERS);
        fileProperties.setShowGrid(false);
        return fileProperties;
    }

    private JTable getImagePropertyTable() {
        ImageCoordinates imageCoordinates = imageSegment.getImageCoordinates();

        String[][] data =
                {{"Identifier: ", imageSegment.getIdentifier()}, {"Source: ", imageSegment.getImageSource()},
                        {"Horizontal Pixels/Block",
                                "" + imageSegment.getNumberOfPixelsPerBlockHorizontal()},
                        {"Vertical Pixels/Block", "" + imageSegment.getNumberOfPixelsPerBlockVertical()},
                        {"Block Size:", "" + imageSegment.getNumberOfPixelsPerBlockHorizontal()
                                * imageSegment.getNumberOfPixelsPerBlockVertical()},
                        {"Blocks/Row: ", "" + imageSegment.getNumberOfBlocksPerRow()},
                        {"Blocks/Column: ", "" + imageSegment.getNumberOfBlocksPerColumn()},
                        {"Rows: ", "" + imageSegment.getNumberOfRows()},
                        {"Columns: ", "" + imageSegment.getNumberOfColumns()},
                        {"Image Compression: ", imageSegment.getImageCompression().name()},
                        {"Image Mode: ", "" + imageSegment.getImageMode()},
                        {"Image Representation: ", imageSegment.getImageRepresentation().name()},
                        {"Image Category: ", imageSegment.getImageCategory().name()},
                        {"Number bpp/Band:", "" + imageSegment.getActualBitsPerPixelPerBand()},
                        {"Actual bpp/Band:", "" + imageSegment.getActualBitsPerPixelPerBand()},
                        {"Bands:", "" + imageSegment.getNumBands()},
                        {"Pixel Value Type:", imageSegment.getPixelValueType().name()},
                        {"Pixel Justification:", imageSegment.getPixelJustification().name()},
                        {"Image Coordinates:", ""}, {"    Coord 0,0:", getImageCoordinate(
                        imageCoordinates,
                        () -> imageCoordinates.getCoordinate00())},
                        {"    Coord 0,max:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinate0MaxCol())},
                        {"    Coord max,max:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinateMaxRowMaxCol())},
                        {"    Coord max,0:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinateMaxRow0())},};

        String[] headers = {"Property", "Value"};
        JTable imageProperties = new JTable(data, headers);
        imageProperties.setShowGrid(false);
        return imageProperties;
    }

    private String getImageCoordinate(ImageCoordinates imageCoordinates,
            Supplier<ImageCoordinatePair> supplier) {
        if (imageCoordinates != null) {
            ImageCoordinatePair icp = supplier.get();
            return String.format("%1$.3f, %2$.3f", icp.getLatitude(), icp.getLongitude());
        }

        return "";
    }

    public ImageSegment getImageSegment() {
        return this.imageSegment;
    }

    public ImagePanel getImagePanel() {
        return imagePanel;
    }
}
