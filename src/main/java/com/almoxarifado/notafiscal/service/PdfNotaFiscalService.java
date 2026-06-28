package com.almoxarifado.notafiscal.service;

import com.almoxarifado.notafiscal.entity.NotaFiscal;
import com.almoxarifado.notafiscal.entity.NotaFiscalItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PdfNotaFiscalService {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color COR_CABECALHO = new Color(30, 64, 120);
    private static final Color COR_LINHA_PAR = new Color(240, 245, 252);

    @Value("${nota-fiscal.pdf-dir}")
    private String pdfDir;

    public String gerarPdf(NotaFiscal nf) {
        try {
            Path dir = Path.of(pdfDir);
            Files.createDirectories(dir);

            String fileName = "NF-%s-%s.pdf".formatted(nf.getNumero(), nf.getSerie());
            Path filePath = dir.resolve(fileName);

            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream(filePath.toFile()));
            doc.open();

            addCabecalho(doc, nf);
            doc.add(Chunk.NEWLINE);
            addDadosEmitente(doc, nf);
            doc.add(Chunk.NEWLINE);
            addDadosDestinatario(doc, nf);
            doc.add(Chunk.NEWLINE);
            addTabelaItens(doc, nf);
            doc.add(Chunk.NEWLINE);
            addRodape(doc, nf);

            doc.close();
            log.info("PDF gerado: {}", filePath);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da Nota Fiscal: " + e.getMessage(), e);
        }
    }

    public byte[] lerPdf(String pdfPath) throws IOException {
        return Files.readAllBytes(Path.of(pdfPath));
    }

    private void addCabecalho(Document doc, NotaFiscal nf) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COR_CABECALHO);
        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{70, 30});

        PdfPCell cellTitulo = new PdfPCell();
        cellTitulo.setBorder(Rectangle.NO_BORDER);
        cellTitulo.setPadding(8);
        cellTitulo.setBackgroundColor(COR_CABECALHO);
        Paragraph titulo = new Paragraph("NOTA FISCAL\nALMOXARIFADO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_LEFT);
        cellTitulo.addElement(titulo);

        PdfPCell cellNumero = new PdfPCell();
        cellNumero.setBorder(Rectangle.NO_BORDER);
        cellNumero.setPadding(8);
        cellNumero.setBackgroundColor(COR_CABECALHO);
        cellNumero.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellNumero.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Font fontNf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        cellNumero.addElement(new Paragraph("NF-e Nº " + nf.getNumero(), fontNf));
        cellNumero.addElement(new Paragraph("Série: " + nf.getSerie(), fontSub));
        cellNumero.addElement(new Paragraph("Emissão: " + nf.getDataEmissao().format(FMT_DATA), fontSub));
        cellNumero.addElement(new Paragraph("Tipo: " + nf.getTipo().name(), fontSub));

        headerTable.addCell(cellTitulo);
        headerTable.addCell(cellNumero);
        doc.add(headerTable);
    }

    private void addDadosEmitente(Document doc, NotaFiscal nf) throws DocumentException {
        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COR_CABECALHO);
        Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell header = new PdfPCell(new Phrase("DADOS DO EMITENTE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        header.setBackgroundColor(COR_CABECALHO);
        header.setPadding(5);
        header.setBorder(Rectangle.NO_BORDER);
        table.addCell(header);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(6);
        cell.setBorderColor(new Color(200, 200, 200));
        cell.addElement(new Paragraph(nf.getEmitenteRazaoSocial(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK)));
        cell.addElement(new Paragraph("CNPJ: " + nf.getEmitenteCnpj(), fontLabel));
        cell.addElement(new Paragraph(nf.getEmitenteEndereco(), fontValue));
        table.addCell(cell);

        doc.add(table);
    }

    private void addDadosDestinatario(Document doc, NotaFiscal nf) throws DocumentException {
        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COR_CABECALHO);
        Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell header = new PdfPCell(new Phrase("DESTINATÁRIO",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        header.setBackgroundColor(COR_CABECALHO);
        header.setPadding(5);
        header.setBorder(Rectangle.NO_BORDER);
        table.addCell(header);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(6);
        cell.setBorderColor(new Color(200, 200, 200));
        cell.addElement(new Paragraph(nf.getDestinatarioNome(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK)));
        if (nf.getDestinatarioCnpjCpf() != null) {
            cell.addElement(new Paragraph("CNPJ/CPF: " + nf.getDestinatarioCnpjCpf(), fontLabel));
        }
        if (nf.getDestinatarioEndereco() != null) {
            cell.addElement(new Paragraph(nf.getDestinatarioEndereco(), fontValue));
        }
        table.addCell(cell);

        doc.add(table);
    }

    private void addTabelaItens(Document doc, NotaFiscal nf) throws DocumentException {
        Font fontColHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font fontCell = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        PdfPTable secHeader = new PdfPTable(1);
        secHeader.setWidthPercentage(100);
        PdfPCell h = new PdfPCell(new Phrase("ITENS DA NOTA FISCAL", fontColHeader));
        h.setBackgroundColor(COR_CABECALHO);
        h.setPadding(5);
        h.setBorder(Rectangle.NO_BORDER);
        secHeader.addCell(h);
        doc.add(secHeader);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{40, 10, 10, 20, 20});

        addHeaderCell(table, "DESCRIÇÃO", fontColHeader, Element.ALIGN_LEFT);
        addHeaderCell(table, "QTD", fontColHeader, Element.ALIGN_CENTER);
        addHeaderCell(table, "UN", fontColHeader, Element.ALIGN_CENTER);
        addHeaderCell(table, "PREÇO UNIT.", fontColHeader, Element.ALIGN_RIGHT);
        addHeaderCell(table, "TOTAL", fontColHeader, Element.ALIGN_RIGHT);

        boolean par = false;
        for (NotaFiscalItem item : nf.getItens()) {
            Color bg = par ? COR_LINHA_PAR : Color.WHITE;
            addDataCell(table, item.getDescricao(), fontCell, Element.ALIGN_LEFT, bg);
            addDataCell(table, formatNum(item.getQuantidade()), fontCell, Element.ALIGN_CENTER, bg);
            addDataCell(table, item.getUnidadeMedida(), fontCell, Element.ALIGN_CENTER, bg);
            addDataCell(table, "R$ " + formatNum(item.getPrecoUnitario()), fontCell, Element.ALIGN_RIGHT, bg);
            addDataCell(table, "R$ " + formatNum(item.getValorTotal()), fontCell, Element.ALIGN_RIGHT, bg);
            par = !par;
        }

        doc.add(table);

        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{75, 25});

        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        totalTable.addCell(emptyCell);

        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        PdfPCell totalCell = new PdfPCell(
                new Phrase("VALOR TOTAL: R$ " + formatNum(nf.getValorTotal()), fontTotal));
        totalCell.setBackgroundColor(COR_CABECALHO);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setPadding(8);
        totalCell.setBorder(Rectangle.NO_BORDER);
        totalTable.addCell(totalCell);

        doc.add(totalTable);
    }

    private void addRodape(Document doc, NotaFiscal nf) throws DocumentException {
        if (nf.getObservacao() != null && !nf.getObservacao().isBlank()) {
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COR_CABECALHO);
            Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);

            PdfPCell header = new PdfPCell(new Phrase("OBSERVAÇÕES",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            header.setBackgroundColor(COR_CABECALHO);
            header.setPadding(5);
            header.setBorder(Rectangle.NO_BORDER);
            table.addCell(header);

            PdfPCell cell = new PdfPCell(new Phrase(nf.getObservacao(), fontValue));
            cell.setPadding(6);
            table.addCell(cell);

            doc.add(table);
        }

        Font fontRodape = FontFactory.getFont(FontFactory.HELVETICA, 7, new Color(120, 120, 120));
        Paragraph rodape = new Paragraph(
                "\nDocumento gerado eletronicamente. Esta NF-e é simulada e não possui validade fiscal.\n" +
                "Emitido em: " + nf.getDataEmissao().format(FMT_DATA),
                fontRodape);
        rodape.setAlignment(Element.ALIGN_CENTER);
        doc.add(rodape);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(50, 90, 150));
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(Color.WHITE);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text, Font font, int align, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(new Color(200, 200, 200));
        table.addCell(cell);
    }

    private String formatNum(BigDecimal val) {
        return val != null ? String.format("%,.2f", val) : "0,00";
    }
}
