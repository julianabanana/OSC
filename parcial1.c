/* simple.c
/*
* A simple kernel module.
*
* To compile, run makefile by entering "make"
*
* Operating System Concepts - 10th Edition
* Copyright John Wiley & Sons - 2018
*/

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/bash.h> //para usar golden ratio prime
#include <linux/gcd.h> para calcular el gcd
#include <linux/jiffies.h> para saber jiffies y hz

/* This function is called when the module is loaded. */
int simple_init(void)
{
    printk(KERN_INFO "Loading Module\n");
    printk(KERN_INFO "%lu\n", GOLDEN_RATIO_PRIME);
    printk(KERN_INFO "Jiffies Inicio: %lu\n", jiffies);
    printk(KERN_INFO "Hz: %d\n", HZ);
    return 0;
}

/* This function is called when the module is removed. */
void simple_exit(void) {
    unsigned long c = gcd(3300,24);
    printk(KERN_INFO "Removing Module\n");
    printk(KERN_INFO "NCD: %lu\n", c);
    printk(KERN_INFO "Jiffies salida: %lu\n", jiffies);
}
/* Macros for registering module entry and exit points. */
module_init(simple_init);
module_exit(simple_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("Simple Module");
MODULE_AUTHOR("SGG");
