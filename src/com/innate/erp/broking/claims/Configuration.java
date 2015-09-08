/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innate.erp.broking.claims;

/**
 *
 * @author Tafadzwa
 */
public enum Configuration {

    PREMIUM_STATUS {
                @Override
                public String toString() {
                    return "PREMIUM STATUS";
                }

            }, CLAIM_STATUS {
                @Override
                public String toString() {
                    return "CLAIM STATUS";
                }

            }, CLAIM_TYPE {
                @Override
                public String toString() {
                    return "CLAIM TYPE";
                }

            }
}
