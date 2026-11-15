from pathlib import Path
import math

from PIL import Image, ImageDraw, ImageFont
from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Inches, Pt, RGBColor


BASE_DIR = Path("documents")
IMG_DIR = BASE_DIR / "diagrammes_avistock"
DOCX_PATH = BASE_DIR / "Diagrammes_Avistock_Memoire_Master.docx"

W, H = 1800, 1200
COL = {
    "blue": "#2E74B5",
    "dark_blue": "#1F4D78",
    "navy": "#0B2545",
    "light_blue": "#E8EEF5",
    "very_light_blue": "#F4F8FC",
    "gray": "#F2F4F7",
    "mid_gray": "#D8DEE6",
    "text": "#1F2937",
    "muted": "#5B6470",
    "green": "#2F7D32",
    "light_green": "#EAF6EC",
    "orange": "#A15C00",
    "light_orange": "#FFF3E0",
    "red": "#9B1C1C",
    "light_red": "#FCECEC",
    "white": "#FFFFFF",
}


def font(size, bold=False):
    candidates = [
        r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf",
        r"C:\Windows\Fonts\calibrib.ttf" if bold else r"C:\Windows\Fonts\calibri.ttf",
    ]
    for path in candidates:
        if Path(path).exists():
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


F_TITLE = font(48, True)
F_SUB = font(27)
F_BOX_TITLE = font(30, True)
F_BOX = font(24)
F_SMALL = font(21)
F_TINY = font(18)


def text_bbox(draw, text, fnt):
    return draw.textbbox((0, 0), text, font=fnt)


def wrap_text(draw, text, fnt, max_width):
    lines = []
    for raw_line in str(text).split("\n"):
        words = raw_line.split()
        if not words:
            lines.append("")
            continue
        line = words[0]
        for word in words[1:]:
            candidate = f"{line} {word}"
            if text_bbox(draw, candidate, fnt)[2] <= max_width:
                line = candidate
            else:
                lines.append(line)
                line = word
        lines.append(line)
    return lines


def draw_centered_lines(draw, box, lines, fnt, fill=COL["text"], line_gap=6):
    x, y, w, h = box
    heights = []
    widths = []
    for line in lines:
        bb = text_bbox(draw, line, fnt)
        widths.append(bb[2] - bb[0])
        heights.append(bb[3] - bb[1])
    total_h = sum(heights) + line_gap * max(len(lines) - 1, 0)
    cy = y + (h - total_h) / 2
    for line, tw, th in zip(lines, widths, heights):
        draw.text((x + (w - tw) / 2, cy), line, font=fnt, fill=fill)
        cy += th + line_gap


def new_canvas(title, subtitle=None):
    img = Image.new("RGB", (W, H), "white")
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, W, 105], fill=COL["very_light_blue"])
    draw.line([0, 105, W, 105], fill=COL["mid_gray"], width=3)
    draw.text((60, 28), title, font=F_TITLE, fill=COL["navy"])
    if subtitle:
        draw.text((62, 82), subtitle, font=F_TINY, fill=COL["muted"])
    return img, draw


def box(draw, xywh, title, body=None, fill=COL["white"], outline=COL["blue"], title_fill=COL["navy"]):
    x, y, w, h = xywh
    draw.rounded_rectangle([x, y, x + w, y + h], radius=20, fill=fill, outline=outline, width=4)
    if body:
        title_lines = wrap_text(draw, title, F_BOX_TITLE, w - 42)
        body_lines = wrap_text(draw, body, F_SMALL, w - 42)
        title_h = len(title_lines) * 34
        start_y = y + 20
        for line in title_lines:
            bb = text_bbox(draw, line, F_BOX_TITLE)
            draw.text((x + (w - (bb[2] - bb[0])) / 2, start_y), line, font=F_BOX_TITLE, fill=title_fill)
            start_y += 36
        start_y += 8
        for line in body_lines:
            bb = text_bbox(draw, line, F_SMALL)
            draw.text((x + (w - (bb[2] - bb[0])) / 2, start_y), line, font=F_SMALL, fill=COL["text"])
            start_y += 27
    else:
        lines = wrap_text(draw, title, F_BOX_TITLE, w - 36)
        draw_centered_lines(draw, (x, y, w, h), lines, F_BOX_TITLE, fill=title_fill)


def ellipse(draw, xywh, title, fill=COL["white"], outline=COL["blue"]):
    x, y, w, h = xywh
    draw.ellipse([x, y, x + w, y + h], fill=fill, outline=outline, width=4)
    lines = wrap_text(draw, title, F_BOX, w - 40)
    draw_centered_lines(draw, (x, y, w, h), lines, F_BOX, fill=COL["text"])


def arrow(draw, start, end, color=COL["dark_blue"], width=5):
    draw.line([start, end], fill=color, width=width)
    angle = math.atan2(end[1] - start[1], end[0] - start[0])
    length = 20
    spread = math.pi / 7
    p1 = (end[0] - length * math.cos(angle - spread), end[1] - length * math.sin(angle - spread))
    p2 = (end[0] - length * math.cos(angle + spread), end[1] - length * math.sin(angle + spread))
    draw.polygon([end, p1, p2], fill=color)


def elbow_arrow(draw, points, color=COL["dark_blue"], width=5):
    for a, b in zip(points, points[1:]):
        draw.line([a, b], fill=color, width=width)
    arrow(draw, points[-2], points[-1], color=color, width=width)


def label(draw, xy, text, fill=COL["muted"], fnt=F_TINY):
    draw.text(xy, text, font=fnt, fill=fill)


def save(img, name):
    IMG_DIR.mkdir(parents=True, exist_ok=True)
    path = IMG_DIR / name
    img.save(path, quality=95)
    return path


def diagram_architecture():
    img, draw = new_canvas("Diagramme 1 - Architecture globale d'Avistock", "Architecture client-serveur et circulation des données")
    boxes = [
        (120, 170, 1560, 110, "Utilisateur / Exploitant", "Accède à l'application depuis le navigateur"),
        (120, 330, 1560, 120, "Frontend React / Vite", "Interface Avistock : tableau de bord, clients, lots, commandes, pertes, rapports, rentabilité, prévision"),
        (120, 510, 1560, 120, "API REST Spring Boot", "Controllers REST exposés sous /api et services métier"),
        (120, 690, 1560, 120, "Couche de persistance", "Repositories Spring Data JPA et entités métier"),
        (120, 870, 1560, 120, "Base H2 persistante", "Fichier local data/avistock-db conservant les informations entre les redémarrages"),
    ]
    fills = [COL["white"], COL["light_blue"], COL["very_light_blue"], COL["gray"], COL["light_green"]]
    for item, fill in zip(boxes, fills):
        x, y, w, h, t, b = item
        box(draw, (x, y, w, h), t, b, fill=fill)
    for y in [280, 450, 630, 810]:
        arrow(draw, (900, y), (900, y + 48))
    label(draw, (1320, 465), "HTTP / JSON", COL["blue"], F_SMALL)
    label(draw, (1320, 645), "Services + JPA", COL["blue"], F_SMALL)
    save(img, "01_architecture_globale.png")


def diagram_use_cases():
    img, draw = new_canvas("Diagramme 2 - Cas d'utilisation", "Fonctions principales utilisées par l'exploitant avicole")
    draw.rounded_rectangle([390, 165, 1690, 1085], radius=25, outline=COL["blue"], width=4, fill=COL["very_light_blue"])
    draw.text((430, 185), "Système Avistock", font=F_BOX_TITLE, fill=COL["navy"])
    # Actor
    draw.ellipse([120, 260, 210, 350], outline=COL["navy"], width=5)
    draw.line([165, 350, 165, 520], fill=COL["navy"], width=5)
    draw.line([165, 390, 100, 460], fill=COL["navy"], width=5)
    draw.line([165, 390, 230, 460], fill=COL["navy"], width=5)
    draw.line([165, 520, 105, 650], fill=COL["navy"], width=5)
    draw.line([165, 520, 225, 650], fill=COL["navy"], width=5)
    draw_centered_lines(draw, (55, 690, 230, 90), ["Exploitant", "avicole"], F_BOX_TITLE, fill=COL["navy"])
    cases = [
        (470, 285, "Gérer les clients"),
        (850, 285, "Créer et suivre les lots"),
        (1230, 285, "Enregistrer les commandes"),
        (470, 505, "Enregistrer les pertes"),
        (850, 505, "Consulter le tableau de bord"),
        (1230, 505, "Analyser la rentabilité"),
        (470, 725, "Générer les rapports"),
        (850, 725, "Configurer les événements"),
        (1230, 725, "Lancer la prévision IA"),
    ]
    centers = []
    for x, y, text in cases:
        ellipse(draw, (x, y, 320, 120), text, fill=COL["white"])
        centers.append((x + 160, y + 60))
    for c in centers:
        draw.line([(240, 455), c], fill=COL["mid_gray"], width=3)
    save(img, "02_cas_utilisation.png")


def entity_box(draw, x, y, w, h, title, attrs, fill=COL["white"]):
    draw.rounded_rectangle([x, y, x + w, y + h], radius=14, fill=fill, outline=COL["blue"], width=4)
    draw.rectangle([x, y, x + w, y + 52], fill=COL["light_blue"], outline=COL["blue"], width=3)
    draw_centered_lines(draw, (x, y + 4, w, 45), [title], F_BOX_TITLE, fill=COL["navy"])
    cy = y + 66
    for attr in attrs:
        draw.text((x + 18, cy), attr, font=F_TINY, fill=COL["text"])
        cy += 29


def relation(draw, start, end, text):
    draw.line([start, end], fill=COL["dark_blue"], width=4)
    mx = (start[0] + end[0]) / 2
    my = (start[1] + end[1]) / 2
    bb = text_bbox(draw, text, F_TINY)
    draw.rounded_rectangle([mx - 70, my - 18, mx + 70, my + 18], radius=8, fill="white", outline=COL["mid_gray"], width=2)
    draw.text((mx - (bb[2] - bb[0]) / 2, my - 12), text, font=F_TINY, fill=COL["muted"])


def diagram_data_model():
    img, draw = new_canvas("Diagramme 3 - Modèle de données", "Entités principales et relations métier")
    entity_box(draw, 80, 170, 360, 220, "Client", ["id", "nom, prénom", "téléphone", "adresse", "dateInscription"])
    entity_box(draw, 720, 155, 400, 250, "Commande", ["id", "dateCommande", "quantité", "prixTotal", "description", "client, lot"])
    entity_box(draw, 1360, 170, 360, 220, "StatutCommande", ["id", "nomStatut"])
    entity_box(draw, 80, 650, 420, 300, "Lot", ["id", "nomLot", "dateArrivee", "quantitéInitiale", "quantitéRestante", "dateMaturitéPrévue", "statutLot", "charges"])
    entity_box(draw, 750, 695, 360, 220, "Perte", ["id", "datePerte", "quantité", "cause", "description", "lot"])
    entity_box(draw, 1320, 660, 400, 280, "Événement", ["id", "nomEvenement", "dateDébut / dateFin", "importance", "coefficientDemande", "baissePostFête"])
    entity_box(draw, 680, 965, 440, 180, "Utilisateur", ["id", "nom, prénom, email", "motDePasse", "dateCréation, actif"], fill=COL["gray"])
    relation(draw, (440, 280), (720, 280), "1 .. *")
    relation(draw, (1120, 280), (1360, 280), "* .. 1")
    relation(draw, (500, 790), (750, 805), "1 .. *")
    relation(draw, (510, 700), (720, 405), "1 .. *")
    relation(draw, (900, 965), (300, 950), "1 .. *")
    relation(draw, (900, 965), (920, 405), "1 .. *")
    draw.line([(1320, 800), (1135, 800)], fill=COL["orange"], width=4)
    draw.text((1138, 760), "utilisé par le\nmodule prédictif", font=F_TINY, fill=COL["orange"])
    save(img, "03_modele_donnees.png")


def diagram_modules():
    img, draw = new_canvas("Diagramme 4 - Modules fonctionnels", "Organisation des écrans et responsabilités")
    box(draw, (650, 185, 500, 110), "Avistock", "Application de pilotage avicole", fill=COL["light_blue"])
    modules = [
        (110, 420, "Tableau de bord", "Indicateurs synthétiques"),
        (510, 420, "Clients", "Référentiel commercial"),
        (910, 420, "Lots", "Production et stock vivant"),
        (1310, 420, "Commandes", "Ventes et décrément du stock"),
        (110, 720, "Pertes", "Suivi sanitaire"),
        (510, 720, "Rentabilité", "CA, charges, bénéfice, marge"),
        (910, 720, "Rapports", "Synthèse par lot, PDF, CSV"),
        (1310, 720, "IA prédictive", "Prévision d'approvisionnement"),
    ]
    for x, y, title, body in modules:
        box(draw, (x, y, 330, 145), title, body, fill=COL["white"])
        arrow(draw, (900, 295), (x + 165, y))
    save(img, "04_modules_fonctionnels.png")


def diagram_order_sequence():
    img, draw = new_canvas("Diagramme 5 - Séquence d'enregistrement d'une commande", "Interaction entre interface, API, service métier et base")
    actors = [
        (120, "Utilisateur"),
        (410, "Frontend React"),
        (720, "CommandeController"),
        (1060, "CommandeService"),
        (1380, "Repositories"),
        (1650, "Base H2"),
    ]
    for x, name in actors:
        box(draw, (x - 110, 170, 220, 70), name, fill=COL["light_blue"])
        draw.line([(x, 240), (x, 1060)], fill=COL["mid_gray"], width=3)
    steps = [
        (300, 320, 120, 410, "Saisie formulaire"),
        (590, 410, 410, 720, "POST /api/commandes"),
        (895, 500, 720, 1060, "creerCommande(request)"),
        (1220, 590, 1060, 1380, "findById(client, lot)"),
        (1515, 680, 1380, 1650, "Lecture client + lot"),
        (1220, 770, 1060, 1380, "save(lot), save(commande)"),
        (895, 860, 1060, 720, "Commande créée"),
        (590, 950, 720, 410, "Réponse JSON"),
        (300, 1020, 410, 120, "Message succès"),
    ]
    for _, y, sx, ex, text in steps:
        arrow(draw, (sx, y), (ex, y), color=COL["dark_blue"], width=4)
        tx = min(sx, ex) + abs(ex - sx) / 2 - 120
        draw.text((tx, y - 34), text, font=F_TINY, fill=COL["text"])
    box(draw, (930, 635, 260, 75), "Contrôle", "stock suffisant ?", fill=COL["light_orange"], outline=COL["orange"])
    save(img, "05_sequence_commande.png")


def diagram_stock_flow():
    img, draw = new_canvas("Diagramme 6 - Flux de stock", "Effet des commandes, pertes et annulations sur le stock vivant")
    box(draw, (660, 165, 480, 110), "Création du lot", "quantitéRestante = quantitéInitiale", fill=COL["light_blue"])
    box(draw, (660, 360, 480, 110), "Stock vivant du lot", "valeur centrale mise à jour", fill=COL["white"])
    arrow(draw, (900, 275), (900, 360))
    box(draw, (120, 560, 430, 120), "Commande validée", "vérifie le stock puis retire la quantité vendue", fill=COL["light_green"], outline=COL["green"])
    box(draw, (680, 560, 440, 120), "Perte enregistrée", "vérifie le stock puis retire la quantité perdue", fill=COL["light_red"], outline=COL["red"])
    box(draw, (1260, 560, 420, 120), "Annulation", "suppression commande/perte : stock recrédité", fill=COL["light_orange"], outline=COL["orange"])
    elbow_arrow(draw, [(335, 560), (335, 430), (660, 430)], color=COL["green"])
    arrow(draw, (900, 470), (900, 560), color=COL["red"])
    elbow_arrow(draw, [(1470, 560), (1470, 430), (1140, 430)], color=COL["orange"])
    box(draw, (660, 820, 480, 120), "Synchronisation statut", "EN_ELEVAGE, PRET_A_VENDRE ou EPUISE", fill=COL["gray"])
    arrow(draw, (900, 680), (900, 820))
    box(draw, (660, 1000, 480, 90), "Rentabilité et rapports", "consomment les données consolidées", fill=COL["very_light_blue"])
    arrow(draw, (900, 940), (900, 1000))
    save(img, "06_flux_stock.png")


def diagram_prediction():
    img, draw = new_canvas("Diagramme 7 - Fonctionnement du système de prédiction IA", "Moteur prédictif à base de règles métier, historique et coefficients")
    inputs = [
        (90, 190, "Date arrivée\nprochain lot"),
        (90, 325, "Durée élevage\n35 à 75 jours"),
        (90, 460, "Fenêtre de vente\n7 à 30 jours"),
        (90, 595, "Historique ventes\n120 jours"),
        (90, 730, "Pertes et mortalité\nhistorique"),
        (90, 865, "Événements / fêtes\ncoefficients"),
    ]
    for x, y, text in inputs:
        box(draw, (x, y, 330, 90), text, fill=COL["white"])
    process = [
        (610, 210, "Calcul période de vente", "date arrivée + durée élevage"),
        (610, 370, "Demande historique", "ventes récentes ou moyenne des lots"),
        (610, 530, "Ajustements événements", "hausse avant fête et baisse post-fête"),
        (610, 690, "Stock projeté + mortalité", "lots mûrs disponibles et taux de survie"),
        (610, 850, "Besoin conseillé", "demande prévisionnelle / survie - stock projeté"),
    ]
    for x, y, title, body in process:
        box(draw, (x, y, 520, 105), title, body, fill=COL["very_light_blue"])
    outputs = [
        (1320, 290, "Quantité conseillée\nde poussins"),
        (1320, 470, "Action\nAUGMENTER / MAINTENIR / REDUIRE"),
        (1320, 650, "Facteurs explicatifs\nstock, mortalité, fêtes"),
        (1320, 830, "Résumé décisionnel\npour l'exploitant"),
    ]
    for x, y, text in outputs:
        box(draw, (x, y, 380, 110), text, fill=COL["light_green"], outline=COL["green"])
    for _, y, _ in inputs:
        arrow(draw, (420, y + 45), (610, 260 if y < 300 else 420 if y < 520 else 580 if y < 670 else 740 if y < 820 else 580), color=COL["mid_gray"], width=4)
    for i in range(len(process) - 1):
        arrow(draw, (870, process[i][1] + 105), (870, process[i + 1][1]), color=COL["dark_blue"], width=5)
    for y in [345, 525, 705, 885]:
        arrow(draw, (1130, 902), (1320, y), color=COL["green"], width=4)
    add_note = "Le terme IA désigne ici un moteur d'aide à la décision : règles métier, historiques et coefficients, pas un modèle ML entraîné."
    box(draw, (510, 1040, 780, 90), "Note méthodologique", add_note, fill=COL["light_orange"], outline=COL["orange"])
    save(img, "07_prediction_ia.png")


def set_cell_shading(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = tc_pr.find(qn("w:shd"))
    if shd is None:
        shd = OxmlElement("w:shd")
        tc_pr.append(shd)
    shd.set(qn("w:fill"), fill.replace("#", ""))


def setup_doc(doc):
    section = doc.sections[0]
    section.page_width = Inches(8.5)
    section.page_height = Inches(11)
    for margin in ("top_margin", "bottom_margin", "left_margin", "right_margin"):
        setattr(section, margin, Inches(0.75))
    section.header_distance = Inches(0.4)
    section.footer_distance = Inches(0.4)

    styles = doc.styles
    normal = styles["Normal"]
    normal.font.name = "Calibri"
    normal._element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
    normal.font.size = Pt(11)
    normal.font.color.rgb = RGBColor.from_string("1F2937")
    normal.paragraph_format.space_after = Pt(6)
    normal.paragraph_format.line_spacing = 1.10
    for name, size, color in [("Heading 1", 16, "2E74B5"), ("Heading 2", 13, "2E74B5"), ("Heading 3", 12, "1F4D78")]:
        st = styles[name]
        st.font.name = "Calibri"
        st._element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
        st.font.size = Pt(size)
        st.font.bold = True
        st.font.color.rgb = RGBColor.from_string(color)
        st.paragraph_format.keep_with_next = True


def add_doc_table(doc):
    table = doc.add_table(rows=1, cols=3)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    headers = ["N°", "Diagramme", "Utilité dans le mémoire"]
    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = h
        set_cell_shading(cell, "F2F4F7")
        cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
        for run in cell.paragraphs[0].runs:
            run.bold = True
    rows = [
        ("1", "Architecture globale", "Présenter la structure client-serveur et la circulation des données."),
        ("2", "Cas d'utilisation", "Montrer les fonctionnalités accessibles à l'exploitant."),
        ("3", "Modèle de données", "Décrire les entités et les relations principales."),
        ("4", "Modules fonctionnels", "Organiser les écrans et responsabilités de l'application."),
        ("5", "Séquence de commande", "Expliquer le traitement d'une vente côté frontend et backend."),
        ("6", "Flux de stock", "Montrer l'impact des ventes, pertes et annulations sur le stock."),
        ("7", "Système de prédiction IA", "Détailler le moteur de recommandation d'approvisionnement."),
    ]
    for row in rows:
        cells = table.add_row().cells
        for i, value in enumerate(row):
            cells[i].text = value
            cells[i].vertical_alignment = WD_ALIGN_VERTICAL.CENTER


def add_picture_section(doc, number, title, path, explanation):
    doc.add_page_break()
    p = doc.add_heading(f"Diagramme {number} - {title}", level=1)
    p.paragraph_format.keep_with_next = True
    doc.add_paragraph(explanation)
    pic_p = doc.add_paragraph()
    pic_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = pic_p.add_run()
    run.add_picture(str(path), width=Inches(6.8))
    cap = doc.add_paragraph(f"Figure {number}. {title}.")
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    for r in cap.runs:
        r.italic = True
        r.font.size = Pt(9)
        r.font.color.rgb = RGBColor.from_string("5B6470")


def build_doc():
    doc = Document()
    setup_doc(doc)
    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = title.add_run("Diagrammes de fonctionnement d'Avistock")
    r.bold = True
    r.font.size = Pt(22)
    r.font.color.rgb = RGBColor.from_string("1F4D78")
    sub = doc.add_paragraph()
    sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    sub.add_run("Complément visuel pour mémoire de Master").italic = True

    doc.add_paragraph(
        "Ce document regroupe les principaux diagrammes permettant d'expliquer la conception et le fonctionnement d'Avistock. "
        "Ils peuvent être utilisés dans le chapitre de conception, d'analyse fonctionnelle ou d'implémentation."
    )
    add_doc_table(doc)

    sections = [
        (1, "Architecture globale d'Avistock", IMG_DIR / "01_architecture_globale.png", "Ce diagramme montre les couches du système : utilisateur, interface React, API Spring Boot, persistance JPA et base H2."),
        (2, "Cas d'utilisation", IMG_DIR / "02_cas_utilisation.png", "Ce diagramme présente les fonctionnalités principales accessibles à l'exploitant avicole."),
        (3, "Modèle de données", IMG_DIR / "03_modele_donnees.png", "Ce diagramme décrit les entités centrales et leurs relations : lots, clients, commandes, pertes, événements et utilisateurs."),
        (4, "Modules fonctionnels", IMG_DIR / "04_modules_fonctionnels.png", "Ce diagramme organise les différents écrans de l'application selon leur responsabilité fonctionnelle."),
        (5, "Séquence d'enregistrement d'une commande", IMG_DIR / "05_sequence_commande.png", "Ce diagramme montre la chaîne de traitement d'une vente, depuis le formulaire jusqu'à l'enregistrement en base."),
        (6, "Flux de stock", IMG_DIR / "06_flux_stock.png", "Ce diagramme explique comment les commandes, pertes et annulations modifient automatiquement le stock vivant."),
        (7, "Fonctionnement du système de prédiction IA", IMG_DIR / "07_prediction_ia.png", "Ce diagramme détaille le moteur prédictif : données d'entrée, traitements, coefficients et recommandation finale."),
    ]
    for section in sections:
        add_picture_section(doc, *section)

    DOCX_PATH.parent.mkdir(parents=True, exist_ok=True)
    doc.save(DOCX_PATH)
    return DOCX_PATH


def main():
    IMG_DIR.mkdir(parents=True, exist_ok=True)
    diagram_architecture()
    diagram_use_cases()
    diagram_data_model()
    diagram_modules()
    diagram_order_sequence()
    diagram_stock_flow()
    diagram_prediction()
    path = build_doc()
    print(path.resolve())


if __name__ == "__main__":
    main()
