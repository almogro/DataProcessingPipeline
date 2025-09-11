#!/usr/bin/env python3
"""
Simple PDF generator for LLD Documentation
"""

from reportlab.lib.pagesizes import letter, A4
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.lib.colors import HexColor
import re

def create_pdf():
    # Create PDF document
    doc = SimpleDocTemplate("docs/LLD_Documentation.pdf", pagesize=A4,
                          rightMargin=72, leftMargin=72,
                          topMargin=72, bottomMargin=18)
    
    # Get styles
    styles = getSampleStyleSheet()
    
    # Create custom styles
    title_style = ParagraphStyle(
        'CustomTitle',
        parent=styles['Heading1'],
        fontSize=24,
        spaceAfter=30,
        textColor=HexColor('#2c3e50'),
        alignment=1  # Center alignment
    )
    
    heading1_style = ParagraphStyle(
        'CustomHeading1',
        parent=styles['Heading1'],
        fontSize=18,
        spaceAfter=12,
        spaceBefore=20,
        textColor=HexColor('#2c3e50')
    )
    
    heading2_style = ParagraphStyle(
        'CustomHeading2',
        parent=styles['Heading2'],
        fontSize=14,
        spaceAfter=8,
        spaceBefore=16,
        textColor=HexColor('#2c3e50')
    )
    
    code_style = ParagraphStyle(
        'Code',
        parent=styles['Code'],
        fontSize=9,
        leftIndent=20,
        rightIndent=20,
        spaceAfter=8,
        spaceBefore=8,
        backColor=HexColor('#f8f9fa')
    )
    
    # Read markdown content
    with open('docs/LLD_Documentation.md', 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Parse content
    story = []
    
    # Add title
    story.append(Paragraph("Low-Level Design (LLD) Documentation", title_style))
    story.append(Paragraph("Functional Data Processing Pipeline with Apache Spark", styles['Heading2']))
    story.append(Spacer(1, 20))
    
    # Split content into sections
    sections = content.split('\n## ')
    
    for i, section in enumerate(sections):
        if i == 0:
            # Skip the first part (already handled)
            continue
            
        lines = section.strip().split('\n')
        section_title = lines[0]
        
        # Add section heading
        story.append(Paragraph(section_title, heading1_style))
        
        # Process content
        content_lines = lines[1:]
        current_code_block = []
        in_code_block = False
        
        for line in content_lines:
            if line.strip().startswith('```'):
                if in_code_block:
                    # End code block
                    if current_code_block:
                        code_text = '\n'.join(current_code_block)
                        story.append(Paragraph(f'<font name="Courier">{code_text}</font>', code_style))
                        current_code_block = []
                    in_code_block = False
                else:
                    # Start code block
                    in_code_block = True
            elif in_code_block:
                current_code_block.append(line)
            elif line.strip().startswith('### '):
                # Subheading
                story.append(Paragraph(line[4:], heading2_style))
            elif line.strip().startswith('- '):
                # Bullet point
                story.append(Paragraph(f"• {line[2:]}", styles['Normal']))
            elif line.strip().startswith('**') and line.strip().endswith('**'):
                # Bold text
                text = line.strip()[2:-2]
                story.append(Paragraph(f"<b>{text}</b>", styles['Normal']))
            elif line.strip():
                # Regular paragraph
                story.append(Paragraph(line, styles['Normal']))
        
        # Add page break between major sections
        if i < len(sections) - 1:
            story.append(PageBreak())
    
    # Build PDF
    doc.build(story)
    print("✅ PDF created successfully: docs/LLD_Documentation.pdf")

if __name__ == "__main__":
    create_pdf()
