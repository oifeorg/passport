(() => {
  document.addEventListener("DOMContentLoaded", () => {
    setCurrentYear(".year");
    loadPdfList("#pdf-list");
  });

  function setCurrentYear(selector = ".year") {
    const currentYear = new Date().getFullYear();
    document.querySelectorAll(selector).forEach(el => {
      el.textContent = currentYear;
    });
  }

  async function loadPdfList(selector) {
    const container = document.querySelector(selector);
    if (!container) return;

    try {
      const response = await fetch("passport-list.json");
      const pdfs = await response.json();

      container.innerHTML = "";

      pdfs.forEach(filename => {
        const name = filename.replace(".pdf", "").replace("-", " ");
        const li = document.createElement("li");
        li.innerHTML = `<a href="${filename}" download>${name}</a>`;
        container.appendChild(li);
      });
    } catch (err) {
      container.innerHTML = `<li>Error loading PDF list: ${err.message}</li>`;
    }
  }
})();