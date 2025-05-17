window.addEventListener('scroll', () => {
    const section = document.querySelector('.section1');
    const image = document.querySelector('.backImg');

    const sectionTop = section.offsetTop;
    const scrollY = window.scrollY;
    const speed = 0.4;

    const offset = scrollY - sectionTop;
    image.style.transform = `translateY(${offset * speed}px)`;
});
console.log("script running");