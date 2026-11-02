document.addEventListener("DOMContentLoaded", () => {
    const freeCheckbox = document.querySelector("[data-free-toggle]");
    const priceField = document.querySelector("[data-price-field]");
    if (freeCheckbox && priceField) {
        const syncPrice = () => {
            priceField.style.display = freeCheckbox.checked ? "none" : "flex";
        };
        freeCheckbox.addEventListener("change", syncPrice);
        syncPrice();
    }

    const resourceType = document.querySelector("[data-resource-type]");
    const linkFields = document.querySelectorAll("[data-resource-link]");
    const fileFields = document.querySelectorAll("[data-resource-file]");
    if (resourceType) {
        const syncResource = () => {
            const isFile = resourceType.value === "FILE";
            linkFields.forEach(field => field.style.display = isFile ? "none" : "flex");
            fileFields.forEach(field => field.style.display = isFile ? "flex" : "none");
        };
        resourceType.addEventListener("change", syncResource);
        syncResource();
    }

    if (window.lucide) {
        window.lucide.createIcons();
    }
});
