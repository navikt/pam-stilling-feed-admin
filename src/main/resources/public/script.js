let utklippstavleTimeout; // Timeout for å tilbakestille knappen etter kopiering

/**
 * Kopier tekst til utklippstavlen
 *
 * Tar inn en CSS-selector for input-feltet og en for knappen og
 * oppdaterer knappen med teksten "✓".
 *
 * @param {string} inputSelector - CSS-selector for input-feltet
 * @param {string} buttonSelector - CSS-selector for knappen
 * @param {string} labelTekst - Fallback-tekst for knappen etter kopiering
 */
async function kopierTilUtklippstavle(inputSelector, buttonSelector, labelTekst) {
    const inputElement = document.querySelector(inputSelector);
    const buttonLabelElement = document.querySelector(buttonSelector).querySelector(".navds-label");

    await navigator.clipboard.writeText(inputElement.value);

    buttonLabelElement.innerText = "\u2713 Kopiert!";

    if (utklippstavleTimeout) {
        clearTimeout(utklippstavleTimeout);
        utklippstavleTimeout = null;
    }
    utklippstavleTimeout = setTimeout(() => {
        buttonLabelElement.innerText = labelTekst;
    }, 600);
}
