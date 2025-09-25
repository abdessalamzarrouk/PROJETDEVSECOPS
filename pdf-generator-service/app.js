// pdf-generator-service/app.js
const express = require('express');
const puppeteer = require('puppeteer');
const bodyParser = require('body-parser'); // To parse JSON request bodies

const app = express();
const port = 3000; // Choose an available port for your PDF service

// Increase limit for potentially large HTML content
app.use(bodyParser.json({ limit: '50mb' }));

app.post('/generate-pdf', async (req, res) => {
    const { htmlContent } = req.body;

    if (!htmlContent) {
        return res.status(400).send('HTML content is required.');
    }

    let browser;
    try {
        browser = await puppeteer.launch({ headless: true, args: ['--no-sandbox', '--disable-setuid-sandbox'] });
        const page = await browser.newPage();
        await page.setContent(htmlContent, { waitUntil: 'networkidle0' }); // Wait for content to render
        const pdfBuffer = await page.pdf({ format: 'A4', landscape: true, printBackground: true }); // Generate PDF as a buffer

        res.set({
            'Content-Type': 'application/pdf',
            'Content-Disposition': 'attachment; filename="generated_report.pdf"'
        });
        res.send(pdfBuffer); // Send the PDF buffer back to the client

    } catch (error) {
        console.error('Error generating PDF:', error);
        res.status(500).send('Error generating PDF: ' + error.message);
    } finally {
        if (browser) {
            await browser.close();
        }
    }
});

app.listen(port, () => {
    console.log(`PDF generator service listening at http://localhost:${port}`);
});