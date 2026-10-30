"""Render the editable academic report as a paginated, linked PDF.

Dependencies: reportlab, Pillow. Run from any directory; paths resolve from this file.
"""
from pathlib import Path
import re
from html import escape
from reportlab.pdfgen import canvas
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, Paragraph, Spacer,
    Table, TableStyle, PageBreak, NextPageTemplate, Image, KeepTogether)
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from PIL import Image as PILImage

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'output/pdf/informe-academico.pdf'
OUT.parent.mkdir(parents=True, exist_ok=True)
NAVY = colors.HexColor('#182c43')
TEAL = colors.HexColor('#087f8c')
MUTED = colors.HexColor('#586c7e')
FONT, BOLD = 'Helvetica', 'Helvetica-Bold'
windows_fonts = Path('C:/Windows/Fonts')
if (windows_fonts / 'segoeui.ttf').exists():
    pdfmetrics.registerFont(TTFont('Segoe', str(windows_fonts/'segoeui.ttf')))
    pdfmetrics.registerFont(TTFont('SegoeBold', str(windows_fonts/'segoeuib.ttf')))
    pdfmetrics.registerFontFamily('Segoe',normal='Segoe',bold='SegoeBold',italic='Segoe',boldItalic='SegoeBold')
    FONT, BOLD = 'Segoe', 'SegoeBold'
styles = getSampleStyleSheet()
styles.add(ParagraphStyle('BodyReport', fontName=FONT, fontSize=10, leading=15,
    textColor=NAVY, spaceAfter=8, splitLongWords=True))
styles.add(ParagraphStyle('TitleReport', fontName=BOLD, fontSize=28, leading=35,
    textColor=NAVY, spaceAfter=22))
styles.add(ParagraphStyle('SectionReport', fontName=BOLD, fontSize=16, leading=22,
    textColor=TEAL, spaceBefore=15, spaceAfter=10, keepWithNext=True))
styles.add(ParagraphStyle('SubsectionReport', fontName=BOLD, fontSize=12, leading=17,
    textColor=NAVY, spaceBefore=10, spaceAfter=7, keepWithNext=True))
styles.add(ParagraphStyle('CellReport', fontName=FONT, fontSize=9, leading=13,
    textColor=NAVY, spaceAfter=0, splitLongWords=True))
styles.add(ParagraphStyle('CaptionReport', fontName=FONT, fontSize=9, leading=13,
    textColor=MUTED, alignment=TA_CENTER, spaceBefore=7, spaceAfter=16))
styles.add(ParagraphStyle('CodeReport', fontName='Courier', fontSize=8, leading=11,
    textColor=NAVY, backColor=colors.HexColor('#f0f5f8'), spaceAfter=9))

def inline(text):
    text = escape(text.strip()).replace('\u2011', '-').replace('\u2013','-').replace('\u2014','-')
    text = re.sub(r'\[([^]]+)\]\(([^)]+)\)', r'<link color="#087f8c" href="\2">\1</link>', text)
    text = re.sub(r'\*\*(.+?)\*\*', r'<b>\1</b>', text)
    text = re.sub(r'`([^`]+)`', r'<font face="Courier">\1</font>', text)
    text = re.sub(r'(?<!["=])(https?://[^\s<]+)', lambda m: '<link color="#087f8c" href="'+m[0]+'">'+m[0]+'</link>' if '</link>' not in text else m[0], text)
    return text

def footer(c, doc):
    width, height = c._pagesize
    c.setStrokeColor(colors.HexColor('#d9e4ea'))
    c.line(44, 40, width-44, 40)
    c.setFont(FONT, 8)
    c.setFillColor(MUTED)
    c.drawString(44, 27, 'TaskFlow | Automatización móvil con Maestro')
    c.drawRightString(width-44, 27, str(doc.page))
    if doc.page > 1:
        c.setFillColor(TEAL)
        c.rect(44, height-32, 28, 3, fill=1, stroke=0)

portrait=(612,792)
landscape=(792,612)
doc=BaseDocTemplate(str(OUT),pagesize=portrait, leftMargin=44,rightMargin=44,
    topMargin=48,bottomMargin=52,title='TaskFlow - Informe académico de pruebas con Maestro',
    author='jefroMMM')
doc.addPageTemplates([
    PageTemplate(id='portrait', frames=[Frame(44,52,524,692,id='p',leftPadding=0,rightPadding=0,topPadding=0,bottomPadding=0)],pagesize=portrait,onPage=footer),
    PageTemplate(id='landscape',frames=[Frame(44,52,704,512,id='l',leftPadding=0,rightPadding=0,topPadding=0,bottomPadding=0)],pagesize=landscape,onPage=footer)])
story=[]
lines=(ROOT/'docs/INFORME_ACADEMICO.md').read_text(encoding='utf-8').splitlines()
i=0
in_cover=True
page_mode='portrait'
while i<len(lines):
    line=lines[i].strip()
    if not line:
        i+=1; continue
    if line.startswith('|'):
        rows=[]
        while i<len(lines) and lines[i].strip().startswith('|'):
            row=[v.strip() for v in lines[i].strip().strip('|').split('|')]
            if not all(re.fullmatch(r'[:\s-]+',v) for v in row): rows.append(row)
            i+=1
        columns=len(rows[0]); wide=columns>=5
        if wide and page_mode != 'landscape':
            story.extend([NextPageTemplate('landscape'), PageBreak(), Paragraph('Comparación de escenarios',styles['SectionReport'])])
        width=704 if wide else 524
        col_widths=([65,105,120,147,140,127] if columns==6 else [width/columns]*columns)
        if columns==2: col_widths=[165,359]
        data=[[Paragraph(inline(v),styles['CellReport']) for v in row] for row in rows]
        table=Table(data,colWidths=col_widths,repeatRows=1,hAlign='LEFT')
        table.setStyle(TableStyle([
            ('BACKGROUND',(0,0),(-1,0),colors.HexColor('#dff1f2')),
            ('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),8),
            ('RIGHTPADDING',(0,0),(-1,-1),8),('TOPPADDING',(0,0),(-1,-1),5 if columns==2 else 8),
            ('BOTTOMPADDING',(0,0),(-1,-1),5 if columns==2 else 8),('LINEBELOW',(0,0),(-1,0),1,TEAL),
            ('LINEBELOW',(0,1),(-1,-1),0.4,colors.HexColor('#d9e4ea')),
            ('ROWBACKGROUNDS',(0,1),(-1,-1),[colors.white,colors.HexColor('#f5f8fa')])]))
        story.extend([table,Spacer(1,12)])
        if wide:
            story.extend([NextPageTemplate('portrait'),PageBreak()])
            page_mode='portrait'
        continue
    if line.startswith('```'):
        code=[]; i+=1
        while i<len(lines) and not lines[i].strip().startswith('```'):
            code.append(lines[i]); i+=1
        for codeline in code:
            # Long shell commands wrap at spaces without clipping page margins.
            story.append(Paragraph(escape(codeline).replace(' ','&nbsp;'),styles['CodeReport']))
        i+=1; continue
    img=re.match(r'!\[([^]]*)\]\(([^)]+)\)',line)
    if img:
        raw=Path(img[2]); path=raw if raw.is_absolute() else ROOT/'docs'/raw
        if not path.exists(): raise FileNotFoundError(path)
        with PILImage.open(path) as im: w,h=im.size
        height=min(500, h*min(270/w,500/h)); width=w*height/h
        image=Image(str(path),width=width,height=height)
        story.append(KeepTogether([image,Paragraph(inline(img[1]),styles['CaptionReport'])]))
        i+=1; continue
    if line.startswith('# '):
        story.extend([Spacer(1,54),Paragraph('INFORME ACADÉMICO',styles['SubsectionReport']),Paragraph(inline(line[2:]),styles['TitleReport'])])
    elif line.startswith('## '):
        title=line[3:]
        if title=='Portada': pass
        else:
            if in_cover: story.append(PageBreak()); in_cover=False
            if title.startswith('4. Diseño'):
                story.extend([NextPageTemplate('landscape'),PageBreak()])
                page_mode='landscape'
            story.append(Paragraph(inline(title),styles['SectionReport']))
    elif line.startswith('### '):
        if line[4:].startswith('Figura '): story.append(PageBreak())
        story.append(Paragraph(inline(line[4:]),styles['SubsectionReport']))
    elif (in_cover and line.startswith('**')) or line.startswith('Maestro. '):
        story.append(Paragraph(inline(line),styles['BodyReport']))
    elif line.startswith('- ') or re.match(r'^\d+\. ',line):
        story.append(Paragraph('• '+inline(re.sub(r'^(?:- |\d+\. )','',line)),styles['BodyReport']))
    else:
        paragraph=[line]; i+=1
        while i<len(lines) and lines[i].strip() and not re.match(r'^(#|\||```|!\[|- |\d+\. )',lines[i].strip()):
            paragraph.append(lines[i].strip()); i+=1
        story.append(Paragraph(inline(' '.join(paragraph)),styles['BodyReport']))
        continue
    i+=1
doc.build(story)
print(OUT)
