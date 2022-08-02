#![no_std]
#![no_main]
#![feature(lang_items)]

#[cfg(test)]
mod tests;

#[cfg(not(test))]
mod lang_item;
