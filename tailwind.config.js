// tailwind.config.js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    // This path is relative to your Spring Boot project root
    // It tells Tailwind to scan your Thymeleaf HTML files for classes
    "./src/main/resources/templates/**/*.html",
    // Add other paths here if you use Tailwind classes in other files (e.g., JS)
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}