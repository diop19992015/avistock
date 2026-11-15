const PDF_PAGE_WIDTH = 595;
const PDF_PAGE_HEIGHT = 842;
const PDF_MARGIN_LEFT = 44;
const PDF_START_Y = 794;
const PDF_FONT_SIZE = 10;
const PDF_LINE_HEIGHT = 14;
const PDF_MAX_CHARS = 88;
const PDF_LINES_PER_PAGE = 50;

export function formatAmountLabel(value) {
  return `${Number(value || 0).toFixed(2)} FCFA`;
}

export function formatPercentLabel(value) {
  return `${Number(value || 0).toFixed(2)} %`;
}

export function formatDateLabel(value) {
  return value || "-";
}

export function formatStatusLabel(status) {
  if (status === "PRET_A_VENDRE") {
    return "Pret a vendre";
  }

  if (status === "EPUISE") {
    return "Epuise";
  }

  if (status === "EN_ELEVAGE") {
    return "En elevage";
  }

  return status || "-";
}

export function downloadReportCsv(report) {
  const lignes = [
    ["AVISTOCK", "Rapport de lot"],
    [],
    ["Lot", report.nomLot || `Lot #${report.lotId}`],
    ["Statut", formatStatusLabel(report.statutLot)],
    ["Date d'arrivee", formatDateLabel(report.dateArrivee)],
    ["Date de maturite prevue", formatDateLabel(report.dateMaturitePrevue)],
    ["Date de generation", formatDateLabel(report.dateGeneration)],
    [],
    ["Indicateur", "Valeur"],
    ["Quantite initiale", report.quantiteInitiale],
    ["Quantite vendue", report.quantiteVendue],
    ["Quantite perdue", report.quantitePerdue],
    ["Quantite restante", report.quantiteRestante],
    ["Chiffre d'affaires", Number(report.chiffreAffaires || 0).toFixed(2)],
    ["Charges totales", Number(report.chargesTotales || 0).toFixed(2)],
    ["Benefice net", Number(report.beneficeNet || 0).toFixed(2)],
    ["Marge (%)", Number(report.margePourcentage || 0).toFixed(2)],
    ["Taux de mortalite (%)", Number(report.tauxMortalite || 0).toFixed(2)],
    [],
    ["VENTES"],
    ["Date", "Client", "Quantite", "Prix total", "Description"],
    ...report.commandes.map((commande) => [
      formatDateLabel(commande.dateCommande),
      commande.nomClient || "-",
      commande.quantite,
      Number(commande.prixTotal || 0).toFixed(2),
      commande.description || ""
    ]),
    [],
    ["PERTES"],
    ["Date", "Quantite", "Cause", "Description"],
    ...report.pertes.map((perte) => [
      formatDateLabel(perte.datePerte),
      perte.quantite,
      perte.cause || "-",
      perte.description || ""
    ])
  ];

  const csvContent = `\uFEFF${lignes
    .map((ligne) => ligne.map(buildCsvValue).join(";"))
    .join("\n")}`;

  downloadBlob(
    `avistock-${buildReportFileStem(report)}.csv`,
    new Blob([csvContent], { type: "text/csv;charset=utf-8;" })
  );
}

export function downloadReportPdf(report) {
  const lines = buildReportLines(report);
  const pages = splitIntoPages(lines, PDF_LINES_PER_PAGE);
  const pdfBytes = buildPdfDocument(pages.map(buildPageStream));

  downloadBlob(
    `avistock-${buildReportFileStem(report)}.pdf`,
    new Blob([pdfBytes], { type: "application/pdf" })
  );
}

function buildCsvValue(value) {
  const normalized = String(value ?? "").replace(/"/g, "\"\"");
  return `"${normalized}"`;
}

function buildReportFileStem(report) {
  const rawName = report?.nomLot || `lot-${report?.lotId ?? "rapport"}`;
  return rawName
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "") || "rapport-lot";
}

function buildReportLines(report) {
  const rawLines = [
    "AVISTOCK",
    "RAPPORT DE LOT",
    "",
    `Lot : ${report.nomLot || `Lot #${report.lotId}`}`,
    `Statut : ${formatStatusLabel(report.statutLot)}`,
    `Date d'arrivee : ${formatDateLabel(report.dateArrivee)}`,
    `Date de maturite prevue : ${formatDateLabel(report.dateMaturitePrevue)}`,
    `Date de generation : ${formatDateLabel(report.dateGeneration)}`,
    "",
    "SYNTHESE",
    `Quantite initiale : ${report.quantiteInitiale}`,
    `Quantite vendue : ${report.quantiteVendue}`,
    `Quantite perdue : ${report.quantitePerdue}`,
    `Quantite restante : ${report.quantiteRestante}`,
    `Chiffre d'affaires : ${formatAmountLabel(report.chiffreAffaires)}`,
    `Charges totales : ${formatAmountLabel(report.chargesTotales)}`,
    `Benefice net : ${formatAmountLabel(report.beneficeNet)}`,
    `Marge : ${formatPercentLabel(report.margePourcentage)}`,
    `Taux de mortalite : ${formatPercentLabel(report.tauxMortalite)}`,
    "",
    "VENTES"
  ];

  if (report.commandes.length === 0) {
    rawLines.push("Aucune vente enregistree pour ce lot.");
  } else {
    report.commandes.forEach((commande, index) => {
      rawLines.push(
        `${index + 1}. ${formatDateLabel(commande.dateCommande)} | ${commande.nomClient || "-"} | `
          + `Qte ${commande.quantite} | ${formatAmountLabel(commande.prixTotal)}`
      );

      if (commande.description) {
        rawLines.push(`   Description : ${commande.description}`);
      }
    });
  }

  rawLines.push("");
  rawLines.push("PERTES");

  if (report.pertes.length === 0) {
    rawLines.push("Aucune perte enregistree pour ce lot.");
  } else {
    report.pertes.forEach((perte, index) => {
      rawLines.push(
        `${index + 1}. ${formatDateLabel(perte.datePerte)} | Qte ${perte.quantite} | ${perte.cause || "-"}`
      );

      if (perte.description) {
        rawLines.push(`   Description : ${perte.description}`);
      }
    });
  }

  return rawLines.flatMap((line) => wrapLine(line, PDF_MAX_CHARS));
}

function wrapLine(text, maxChars) {
  if (!text) {
    return [""];
  }

  const indentationMatch = text.match(/^\s*/);
  const indentation = indentationMatch ? indentationMatch[0] : "";
  const words = text.trim().split(/\s+/);
  const lines = [];
  let currentLine = indentation;

  words.forEach((word) => {
    if (word.length > maxChars) {
      if (currentLine.trim()) {
        lines.push(currentLine);
        currentLine = indentation;
      }

      for (let index = 0; index < word.length; index += maxChars) {
        lines.push(`${indentation}${word.slice(index, index + maxChars)}`);
      }
      return;
    }

    const candidate = currentLine.trim()
      ? `${currentLine} ${word}`
      : `${indentation}${word}`;

    if (candidate.length > maxChars && currentLine.trim()) {
      lines.push(currentLine);
      currentLine = `${indentation}${word}`;
    } else {
      currentLine = candidate;
    }
  });

  if (currentLine) {
    lines.push(currentLine);
  }

  return lines;
}

function splitIntoPages(lines, linesPerPage) {
  const pages = [];

  for (let index = 0; index < lines.length; index += linesPerPage) {
    pages.push(lines.slice(index, index + linesPerPage));
  }

  return pages.length > 0 ? pages : [["Aucun contenu disponible."]];
}

function buildPageStream(lines) {
  const safeLines = lines.length > 0 ? lines : [""];
  const chunks = [
    "BT\n",
    `/F1 ${PDF_FONT_SIZE} Tf\n`,
    `${PDF_LINE_HEIGHT} TL\n`,
    `${PDF_MARGIN_LEFT} ${PDF_START_Y} Td\n`
  ];

  safeLines.forEach((line, index) => {
    if (index > 0) {
      chunks.push("T*\n");
    }

    chunks.push(`(${escapePdfText(line)}) Tj\n`);
  });

  chunks.push("ET");
  return chunks.join("");
}

function buildPdfDocument(pageStreams) {
  const fontObjectId = 3;
  const objects = new Map();
  const pageObjectIds = [];

  objects.set(1, "<< /Type /Catalog /Pages 2 0 R >>");

  objects.set(
    fontObjectId,
    "<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"
  );

  let nextObjectId = 4;

  pageStreams.forEach((stream) => {
    const pageObjectId = nextObjectId++;
    const contentObjectId = nextObjectId++;

    pageObjectIds.push(pageObjectId);
    objects.set(
      pageObjectId,
      `<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${PDF_PAGE_WIDTH} ${PDF_PAGE_HEIGHT}] `
        + `/Resources << /Font << /F1 ${fontObjectId} 0 R >> >> /Contents ${contentObjectId} 0 R >>`
    );
    objects.set(contentObjectId, {
      stream
    });
  });

  objects.set(
    2,
    `<< /Type /Pages /Kids [${pageObjectIds.map((id) => `${id} 0 R`).join(" ")}] `
      + `/Count ${pageObjectIds.length} >>`
  );

  const byteChunks = [stringToBytes("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n")];
  const offsets = [0];
  let length = byteChunks[0].length;
  const maxObjectId = Math.max(...objects.keys());

  for (let objectId = 1; objectId <= maxObjectId; objectId += 1) {
    const objectValue = objects.get(objectId);
    offsets[objectId] = length;

    byteChunks.push(stringToBytes(`${objectId} 0 obj\n`));
    length += byteChunks[byteChunks.length - 1].length;

    if (typeof objectValue === "string") {
      byteChunks.push(stringToBytes(objectValue));
      length += byteChunks[byteChunks.length - 1].length;
    } else {
      const streamBytes = stringToBytes(objectValue.stream);
      byteChunks.push(
        stringToBytes(`<< /Length ${streamBytes.length} >>\nstream\n`),
        streamBytes,
        stringToBytes("\nendstream")
      );
      length += byteChunks[byteChunks.length - 3].length
        + byteChunks[byteChunks.length - 2].length
        + byteChunks[byteChunks.length - 1].length;
    }

    byteChunks.push(stringToBytes("\nendobj\n"));
    length += byteChunks[byteChunks.length - 1].length;
  }

  const xrefOffset = length;
  byteChunks.push(stringToBytes(`xref\n0 ${maxObjectId + 1}\n`));
  byteChunks.push(stringToBytes("0000000000 65535 f \n"));

  for (let objectId = 1; objectId <= maxObjectId; objectId += 1) {
    byteChunks.push(
      stringToBytes(`${String(offsets[objectId]).padStart(10, "0")} 00000 n \n`)
    );
  }

  byteChunks.push(
    stringToBytes(
      `trailer\n<< /Size ${maxObjectId + 1} /Root 1 0 R >>\nstartxref\n${xrefOffset}\n%%EOF`
    )
  );

  return concatenateBytes(byteChunks);
}

function escapePdfText(text) {
  return String(text ?? "")
    .replace(/\\/g, "\\\\")
    .replace(/\(/g, "\\(")
    .replace(/\)/g, "\\)");
}

function stringToBytes(value) {
  const bytes = [];

  for (const character of value) {
    const codePoint = character.codePointAt(0);
    bytes.push(codePoint <= 255 ? codePoint : 63);
  }

  return Uint8Array.from(bytes);
}

function concatenateBytes(chunks) {
  const totalLength = chunks.reduce((sum, chunk) => sum + chunk.length, 0);
  const merged = new Uint8Array(totalLength);
  let offset = 0;

  chunks.forEach((chunk) => {
    merged.set(chunk, offset);
    offset += chunk.length;
  });

  return merged;
}

function downloadBlob(filename, blob) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
